package org.kmontano.minidex.services;

import org.kmontano.minidex.dto.PokemonDTO;
import org.kmontano.minidex.models.Pokemon;

import java.util.List;
import java.util.Optional;

/**
 * Interfaz de servicio para manejar la lógica de negocio de Pokemon.
 */
public interface PokemonService {
    Pokemon save(Pokemon pokemon);
    Optional<Pokemon> findByName(String name);
    Optional<Pokemon> findById(Long id);
    List<PokemonDTO> findAll();
    List<Pokemon> findAllByIdWithTypes(List<Long> pokemonsIds);
}
