package org.kmontano.minidex.repositories;

import org.kmontano.minidex.models.Pokemon;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

/**
 * Repositorio JPA para la entidad Pokemon.
 * Incluye consultas personalizadas para cargar pokemons junto con sus tipos.
 */
public interface PokemonRepository extends JpaRepository<Pokemon, Long> {
    @Query("SELECT p FROM Pokemon p LEFT JOIN FETCH p.types WHERE p.name=:name")
    Optional<Pokemon> findByName(@Param("name") String name);

    @Query("SELECT DISTINCT p FROM Pokemon p LEFT JOIN FETCH p.types WHERE p.id IN :ids")
    List<Pokemon> findAllByIdWithTypes(@Param("ids") List<Long> ids);
}
