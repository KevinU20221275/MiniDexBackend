package org.kmontano.minidex.repositories;

import org.kmontano.minidex.models.PokemonType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface TypeRepository extends JpaRepository<PokemonType, Long> {
    Optional<PokemonType> findByName(String name);
}
