package org.kmontano.minidex.application.service;

import org.kmontano.minidex.domain.pokemon.PokemonTypeCache;

public interface PokemonTypeCacheService {
    PokemonTypeCache getType(String typeUrl);
}
