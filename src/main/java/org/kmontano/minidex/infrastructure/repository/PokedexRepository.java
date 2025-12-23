package org.kmontano.minidex.infrastructure.repository;

import org.kmontano.minidex.domain.pokedex.Pokedex;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Repositorio Mongo para la entidad Pokemon.
 */
@Repository
public interface PokedexRepository extends MongoRepository<Pokedex, String> {
   Optional<Pokedex> getPokedexByOwnerId(@Param("owner") String owner);
}
