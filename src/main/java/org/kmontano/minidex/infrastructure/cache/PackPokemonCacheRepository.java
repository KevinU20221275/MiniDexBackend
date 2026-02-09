package org.kmontano.minidex.infrastructure.cache;

import org.springframework.data.mongodb.repository.MongoRepository;

public interface PackPokemonCacheRepository extends MongoRepository<PackPokemonCache, Integer> {
}
