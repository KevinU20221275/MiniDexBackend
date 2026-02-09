package org.kmontano.minidex.infrastructure.repository;

import org.kmontano.minidex.domain.pokemon.PokemonTypeCache;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface PokemonTypeCacheRepository extends MongoRepository<PokemonTypeCache, Integer> {
}
