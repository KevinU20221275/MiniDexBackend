package org.kmontano.minidex.application.serviceImpl;

import org.kmontano.minidex.domain.pokedex.Pokedex;
import org.kmontano.minidex.domain.pokemon.Pokemon;
import org.kmontano.minidex.factory.PokemonFactory;
import org.kmontano.minidex.infrastructure.mapper.PokemonResponse;
import org.kmontano.minidex.infrastructure.repository.PokedexRepository;
import org.kmontano.minidex.application.service.PokedexService;
import org.kmontano.minidex.infrastructure.api.PokemonApiClient;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.Optional;

@Service
public class PokedexServiceImpl implements PokedexService {
    private final PokedexRepository repository;
    private final PokemonFactory pokemonFactory;
    private final PokemonApiClient pokemonApiClient;

    public PokedexServiceImpl(PokedexRepository repository, PokemonFactory pokemonFactory, EvolutionServiceImpl evolutionService, PokemonApiClient pokemonApiClient) {
        this.repository = repository;
        this.pokemonFactory = pokemonFactory;
        this.pokemonApiClient = pokemonApiClient;
    }

    @Override
    public Optional<Pokedex> getPokedexByOwner(String owner) {
        return repository.getPokedexByOwnerId(owner);
    }

    @Override
    public Optional<Pokedex> addPokemon(String owner, Pokemon pokemon) {
        Optional<Pokedex> pokedex = getPokedexByOwner(owner);
        if (pokedex.isPresent()){
            Pokedex pokedexToUpdate = pokedex.get();
            pokedexToUpdate.getPokemons().add(pokemon);
            repository.save(pokedexToUpdate);
            return Optional.of(pokedexToUpdate);
        } else {
            Pokedex newPokedex = new Pokedex();
            newPokedex.setOwnerId(owner);
            newPokedex.getPokemons().add(pokemon);
            repository.save(newPokedex);
            return Optional.of(newPokedex);
        }
    }

    @Override
    public Optional<Pokedex> update(Pokedex pokedex) {
        return Optional.of(repository.save(pokedex));
    }

    @Override
    public Optional<Pokedex> removePokemon(String owner, String pokemonId) {
        Optional<Pokedex> pokedex = getPokedexByOwner(owner);
        if (pokedex.isPresent()){
            Pokedex pokedexToUpdate = pokedex.get();
            boolean remove = pokedexToUpdate.getPokemons().removeIf(p -> p.getUuid().equals(pokemonId));

            if (!remove) {
                throw new ResponseStatusException(HttpStatus.NOT_FOUND, "El Pokémon no está en la Pokédex del entrenador");
            }

            return Optional.of(repository.save(pokedexToUpdate));
        }

        return Optional.empty();
    }

    @Override
    public Optional<Pokedex> evolPokemon(String owner, String pokemonId) {
        Pokedex pokedex = repository.getPokedexByOwnerId(owner)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Pokedex no encontrada"
                ));

        Pokemon oldPokemon = pokedex.getPokemons().stream()
                .filter(p -> p.getUuid().equals(pokemonId))
                .findFirst()
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Pokemon no encontrado"
                ));

        PokemonResponse evolvedResponse = pokemonApiClient.getPokemonByName(oldPokemon.getName());

        Pokemon evolvedPokemon = pokemonFactory.toFullPokemon(evolvedResponse, oldPokemon.getShiny());


        evolvedPokemon.setUuid(oldPokemon.getUuid());
        evolvedPokemon.setLevel(oldPokemon.getLevel());

        pokedex.getPokemons().remove(oldPokemon);
        pokedex.getPokemons().add(evolvedPokemon);

        return Optional.of(repository.save(pokedex));
    }

    @Override
    public Optional<Pokedex> addPokemonToTeam(String owner, String pokemonId) {
        Pokedex pokedex = repository.getPokedexByOwnerId(owner)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Pokedex no encontrada"
                ));

        pokedex.addPokemonToTeam(pokemonId);

        return Optional.of(repository.save(pokedex));
    }

    @Override
    public Optional<Pokedex> removePokemonFromTeam(String owner, String pokemonId) {
        Pokedex pokedex = repository.getPokedexByOwnerId(owner)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Pokedex no encontrada"
                ));

        pokedex.removePokemonFromTeam(pokemonId);

        return Optional.of(repository.save(pokedex));
    }
}
