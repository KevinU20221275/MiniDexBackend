package org.kmontano.minidex.application.serviceImpl;

import org.kmontano.minidex.domain.pokedex.Pokedex;
import org.kmontano.minidex.domain.pokemon.Pokemon;
import org.kmontano.minidex.dto.response.PackPokemon;
import org.kmontano.minidex.exception.ResourceNotFoundException;
import org.kmontano.minidex.factory.PokemonFactory;
import org.kmontano.minidex.infrastructure.mapper.PokemonResponse;
import org.kmontano.minidex.infrastructure.repository.PokedexRepository;
import org.kmontano.minidex.application.service.PokedexService;
import org.kmontano.minidex.infrastructure.api.PokemonApiClient;
import org.springframework.stereotype.Service;

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

    /**
     * Creates a new PokedexServiceImpl.
     *
     * @param repository persistence layer for Pokédex data
     * @param pokemonFactory factory used to build Pokémon domain objects
     * @param pokemonApiClient client used to fetch Pokémon data from external API
     */
    public PokedexServiceImpl(PokedexRepository repository, PokemonFactory pokemonFactory, EvolutionServiceImpl evolutionService, PokemonApiClient pokemonApiClient) {
        this.repository = repository;
        this.pokemonFactory = pokemonFactory;
        this.pokemonApiClient = pokemonApiClient;
    }

    /**
     * Retrieves the Pokédex owned by a trainer.
     *
     * @param owner trainer identifier
     * @return optional Pokédex
     */
    @Override
    public Optional<Pokedex> getPokedexByOwner(String owner) {
        return repository.getPokedexByOwnerId(owner);
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
    public Optional<Pokedex> addPokemon(String owner, Pokemon pokemon) {
        Optional<Pokedex> pokedex = getPokedexByOwner(owner);
        if (pokedex.isPresent()){
            Pokedex pokedexToUpdate = pokedex.get();
            pokedexToUpdate.getPokemons().add(pokemon);
            repository.save(pokedexToUpdate);

            return Optional.of(pokedexToUpdate);
        }

        Pokedex newPokedex = new Pokedex();
        newPokedex.setOwnerId(owner);
        newPokedex.getPokemons().add(pokemon);
        repository.save(newPokedex);

        return Optional.of(newPokedex);
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
        Pokedex pokedex = repository.getPokedexByOwnerId(ownerId)
                .orElseThrow(() -> new ResourceNotFoundException("Pokedex not found"));

        for (int i = 0; i < pokemons.size(); i++){
            PokemonResponse p = pokemonApiClient.getPokemonByName(pokemons.get(i).getName());
            Pokemon pokemon = pokemonFactory.toFullPokemon(p, pokemons.get(i).isShiny());
            pokedex.getPokemons().add(pokemon);
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
     * @param owner trainer identifier
     * @param pokemonId Pokémon unique identifier
     * @return updated Pokédex
     */
    @Override
    public Optional<Pokedex> removePokemon(String owner, String pokemonId) {
        Optional<Pokedex> pokedex = getPokedexByOwner(owner);
        if (pokedex.isPresent()){
            Pokedex pokedexToUpdate = pokedex.get();
            boolean remove = pokedexToUpdate.getPokemons().removeIf(p -> p.getUuid().equals(pokemonId));

            if (!remove) {
                throw new ResourceNotFoundException("Pokemon is not in pokedex");
            }

            return Optional.of(repository.save(pokedexToUpdate));
        }

        return Optional.empty();
    }

    /**
     * Evolves a Pokémon inside a trainer's Pokédex.
     * The evolved Pokémon keeps the same UUID and level.
     *
     * @param owner trainer identifier
     * @param pokemonId Pokémon unique identifier
     * @return updated Pokédex
     */
    @Override
    public Optional<Pokedex> evolPokemon(String owner, String pokemonId) {
        Pokedex pokedex = repository.getPokedexByOwnerId(owner)
                .orElseThrow(() -> new ResourceNotFoundException("Pokedex not found"));

        Pokemon oldPokemon = pokedex.getPokemons().stream()
                .filter(p -> p.getUuid().equals(pokemonId))
                .findFirst()
                .orElseThrow(() -> new ResourceNotFoundException("Pokemon not found"));

        PokemonResponse evolvedResponse = pokemonApiClient.getPokemonByName(oldPokemon.getName());

        Pokemon evolvedPokemon = pokemonFactory.toFullPokemon(evolvedResponse, oldPokemon.getShiny());


        evolvedPokemon.setUuid(oldPokemon.getUuid());
        evolvedPokemon.setLevel(oldPokemon.getLevel());

        pokedex.getPokemons().remove(oldPokemon);
        pokedex.getPokemons().add(evolvedPokemon);

        return Optional.of(repository.save(pokedex));
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
     * @return updated Pokédex
     */
    @Override
    public Optional<Pokedex> removePokemonFromTeam(String owner, String pokemonId) {
        Pokedex pokedex = repository.getPokedexByOwnerId(owner)
                .orElseThrow(() -> new ResourceNotFoundException("Pokedex not found"));

        pokedex.removePokemonFromTeam(pokemonId);

        return Optional.of(repository.save(pokedex));
    }
}
