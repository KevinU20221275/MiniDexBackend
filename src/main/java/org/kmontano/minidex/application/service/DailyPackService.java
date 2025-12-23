package org.kmontano.minidex.application.service;

import org.kmontano.minidex.domain.pokemon.PackPokemon;
import org.kmontano.minidex.domain.trainer.DailyPackStatus;
import org.kmontano.minidex.domain.trainer.Envelope;

import java.util.List;

public interface DailyPackService {
    DailyPackStatus initialize();
    List<Envelope> buildEnvelopes(List<PackPokemon> pokemons);
    DailyPackStatus resetIfNeeded(DailyPackStatus status);
}
