package org.kmontano.minidex.application.service;

import org.kmontano.minidex.domain.pokedex.Pokedex;
import org.kmontano.minidex.domain.pokemon.Pokemon;
import org.kmontano.minidex.domain.trainer.Trainer;
import org.kmontano.minidex.dto.response.*;
import org.springframework.data.domain.Page;

import java.util.List;
import java.util.Optional;

public interface PokedexService {
    Pokedex getPokedexByOwner(String owner);
    Pokedex addPokemon(String owner, Pokemon pokemon);
    TransferPokemonResponse removePokemon(Trainer trainer, String pokemonId);
    EvolutionPokemonResponse evolutionPokemon(Trainer owner, String pokemonId);
    Optional<Pokedex> addPokemonToTeam(String owner, String pokemonId);
    void removePokemonFromTeam(String owner, String pokemonId);
    Optional<Pokedex> update(Pokedex pokedex);
    void addPokemonsFromEnvelope(List<PackPokemon> pokemons, String ownerId);
    PokedexPageDTO getFilteredPokedex(String trainerId, int page, int size, String type, Boolean shiny, Boolean orderByPokedex);
}
