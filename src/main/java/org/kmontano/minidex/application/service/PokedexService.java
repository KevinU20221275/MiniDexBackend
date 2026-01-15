package org.kmontano.minidex.application.service;

import org.kmontano.minidex.domain.pokedex.Pokedex;
import org.kmontano.minidex.domain.pokemon.Pokemon;
import org.kmontano.minidex.domain.trainer.Trainer;
import org.kmontano.minidex.dto.response.PackPokemon;

import java.util.List;
import java.util.Optional;

public interface PokedexService {
    Optional<Pokedex> getPokedexByOwner(String owner);
    Optional<Pokedex> addPokemon(String owner, Pokemon pokemon);
    Optional<Pokedex> removePokemon(String owner, String pokemonId);
    Optional<Pokemon> evolPokemon(Trainer owner, String pokemonId);
    Optional<Pokedex> addPokemonToTeam(String owner, String pokemonId);
    Optional<Pokedex> removePokemonFromTeam(String owner, String pokemonId);
    Optional<Pokedex> update(Pokedex pokedex);
    void addPokemonsFromEnvelope(List<PackPokemon> pokemons, String ownerId);
}
