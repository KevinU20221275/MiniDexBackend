package org.kmontano.minidex.application.service;

import org.kmontano.minidex.dto.response.PackPokemon;

import java.util.List;
import java.util.Random;

public interface DailyPackService {
    PackPokemon getRandomPackPokemon();
    PackPokemon getRandomPackPokemon(Double customShinyRate);
    List<PackPokemon> generateDailyPackPokemons();
    List<PackPokemon> getPokemonsFromBoostedPack();
    PackPokemon generateDailySpecial(Random random);
}
