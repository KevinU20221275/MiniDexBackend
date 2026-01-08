package org.kmontano.minidex.application.service;

import org.kmontano.minidex.dto.response.PackPokemon;
import org.kmontano.minidex.domain.trainer.DailyPackStatus;
import org.kmontano.minidex.domain.trainer.Envelope;
import org.kmontano.minidex.dto.response.SpecialPokemonDTO;

import java.util.List;
import java.util.Random;

public interface DailyPackService {
    DailyPackStatus initialize();
    List<Envelope> buildEnvelopes(List<PackPokemon> pokemons);
    DailyPackStatus resetIfNeeded(DailyPackStatus status);
    PackPokemon getRandomPackPokemon();
    PackPokemon getRandomPackPokemon(Double customShinyRate);
    List<PackPokemon> generateDailyPackPokemons();
    List<PackPokemon> getPokemonsFromBoostedPack();
    PackPokemon generateDailySpecial(Random random);
}
