package org.kmontano.minidex.application.serviceImpl;

import org.kmontano.minidex.application.service.RewardService;
import org.kmontano.minidex.domain.pokedex.Pokedex;
import org.kmontano.minidex.domain.pokemon.Pokemon;
import org.kmontano.minidex.domain.trainer.Trainer;
import org.kmontano.minidex.dto.response.PackPokemon;
import org.kmontano.minidex.exception.DomainConflictException;
import org.kmontano.minidex.exception.ResourceNotFoundException;
import org.kmontano.minidex.factory.PokemonFactory;
import org.kmontano.minidex.infrastructure.mapper.PokemonResponse;
import org.kmontano.minidex.infrastructure.repository.PokedexRepository;
import org.kmontano.minidex.application.service.PokedexService;
import org.kmontano.minidex.infrastructure.api.PokemonApiClient;
import org.kmontano.minidex.infrastructure.repository.TrainerRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

/**
 * Implementation of {@link PokedexService}.
 *
 * This service contains the business logic related to a trainer's Pokédex,
 * including adding, removing, evolving Pokémon and managing the active team.
 *
 * This class coordinates between:
 * - The persistence layer (PokedexRepository)
 * - External Pokémon API access
 * - Pokémon domain creation via factories
 */
@Service
public class PokedexServiceImpl implements PokedexService {
    private final PokedexRepository repository;
    private final PokemonFactory pokemonFactory;
    private final PokemonApiClient pokemonApiClient;
    private final TrainerRepository trainerRepository;
    private final RewardService rewardService;

    /**
     * Creates a new PokedexServiceImpl.
     *
     * @param repository persistence layer for Pokédex data
     * @param pokemonFactory factory used to build Pokémon domain objects
     * @param pokemonApiClient client used to fetch Pokémon data from external API
     */
    public PokedexServiceImpl(PokedexRepository repository, PokemonFactory pokemonFactory, PokemonApiClient pokemonApiClient, TrainerRepository trainerRepository, RewardService rewardService) {
        this.repository = repository;
        this.pokemonFactory = pokemonFactory;
        this.pokemonApiClient = pokemonApiClient;
        this.trainerRepository = trainerRepository;
        this.rewardService = rewardService;
    }

    /**
     * Retrieves the Pokédex owned by a trainer.
     *
     * @param owner trainer identifier
     * @return optional Pokédex
     */
    @Override
    public Pokedex getPokedexByOwner(String owner) {
        return repository.getPokedexByOwnerId(owner)
                .orElseGet(() -> repository.save(new Pokedex(owner)));
    }

    /**
     * Adds a single Pokémon to a trainer's Pokédex.
     * If the Pokédex does not exist, a new one is created.
     *
     * @param owner trainer identifier
     * @param pokemon Pokémon to add
     * @return updated Pokédex
     */
    @Override
    public Pokedex addPokemon(String owner, Pokemon pokemon) {
        Pokedex pokedex = getPokedexByOwner(owner);
        pokedex.addPokemon(pokemon);

        return repository.save(pokedex);
    }

    /**
     * Adds multiple Pokémon obtained from a daily envelope
     * to the trainer's Pokédex.
     *
     * Each PackPokemon is transformed into a full Pokémon
     * using data fetched from the external Pokémon API.
     *
     * @param pokemons list of Pokémon obtained from the envelope
     * @param ownerId trainer identifier
     */
    @Override
    public void addPokemonsFromEnvelope(List<PackPokemon> pokemons, String ownerId) {
        Pokedex pokedex = getPokedexByOwner(ownerId);

        for (int i = 0; i < pokemons.size(); i++){
            PokemonResponse p = pokemonApiClient.getPokemonByName(pokemons.get(i).getName());
            Pokemon pokemon = pokemonFactory.toFullPokemon(p, pokemons.get(i).isShiny());
            pokedex.addPokemon(pokemon);
        }

        repository.save(pokedex);
    }

    /**
     * Updates and persists a Pokédex.
     *
     * @param pokedex Pokédex to update
     * @return updated Pokédex
     */
    @Override
    public Optional<Pokedex> update(Pokedex pokedex) {
        return Optional.of(repository.save(pokedex));
    }

    /**
     * Removes a Pokémon from a trainer's Pokédex.
     *
     * @param trainer authenticate trainer
     * @param pokemonId Pokémon unique identifier
     */
    @Override
    public void removePokemon(Trainer trainer, String pokemonId) {
        Pokedex pokedex = getPokedexByOwner(trainer.getId());

        Pokemon pokemon = pokedex.removePokemonFromPokedex(pokemonId);
        int coins = rewardService.calculateRewardByTransferPokemon(pokemon);
        trainer.addXp(coins);
        trainer.addCoins(coins);
        repository.save(pokedex);
        trainerRepository.save(trainer);
    }

    /**
     * Evolves a Pokémon inside a trainer's Pokédex.
     * The evolved Pokémon keeps the same UUID and level.
     *
     * @param trainer authenticate trainer owner of pokedex
     * @param pokemonId Pokémon unique identifier
     * @return updated Pokémon
     */
    @Override
    @Transactional
    public Pokemon evolPokemon(Trainer trainer, String pokemonId) {
        final int EVOLUTION_COST = 100;

        Pokedex pokedex = getPokedexByOwner(trainer.getId());

        Pokemon pokemonToEvol = pokedex.findById(pokemonId);

        trainer.subtractCoins(EVOLUTION_COST);

        if (pokemonToEvol.getNextEvolution() == null) throw new DomainConflictException("Pokemon can't evolve");

        PokemonResponse evolvedResponse = pokemonApiClient.getPokemonByName(pokemonToEvol.getNextEvolution());

        Pokemon evolvedPokemon = pokemonFactory.toFullPokemon(evolvedResponse, pokemonToEvol.getShiny());

        pokemonToEvol.evolveTo(evolvedPokemon);

        trainerRepository.save(trainer);
        repository.save(pokedex);

        return pokemonToEvol;
    }

    /**
     * Adds a Pokémon to the trainer's active team.
     *
     * @param owner trainer identifier
     * @param pokemonId Pokémon unique identifier
     * @return updated Pokédex
     */
    @Override
    public Optional<Pokedex> addPokemonToTeam(String owner, String pokemonId) {
        Pokedex pokedex = repository.getPokedexByOwnerId(owner)
                .orElseThrow(() -> new ResourceNotFoundException("Pokedex not found"));

        pokedex.addPokemonToTeam(pokemonId);

        return Optional.of(repository.save(pokedex));
    }

    /**
     * Removes a Pokémon from the trainer's active team.
     *
     * @param owner trainer identifier
     * @param pokemonId Pokémon unique identifier
     */
    @Override
    public void removePokemonFromTeam(String owner, String pokemonId) {
        Pokedex pokedex = repository.getPokedexByOwnerId(owner)
                .orElseThrow(() -> new ResourceNotFoundException("Pokedex not found"));

        pokedex.removePokemonFromTeam(pokemonId);

        repository.save(pokedex);
    }
}
