package org.kmontano.minidex.application.service;

import org.kmontano.minidex.dto.response.DailySpecialPokemonInfoDTO;
import org.kmontano.minidex.dto.response.PackPokemon;
import org.kmontano.minidex.infrastructure.mapper.PokemonResponse;

import java.util.List;
import java.util.Random;

public interface DailyPackService {
    PackPokemon getRandomPackPokemon();
    PackPokemon getRandomPackPokemon(Double customShinyRate);
    List<PackPokemon> generateDailyPackPokemons();
    List<PackPokemon> getPokemonsFromBoostedPack();
    DailySpecialPokemonInfoDTO generateDailySpecial(Random random);
}
