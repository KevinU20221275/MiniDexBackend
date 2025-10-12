package org.kmontano.minidex.services;


import org.kmontano.minidex.dto.*;
import org.kmontano.minidex.models.Pokemon;
import org.kmontano.minidex.models.Trainer;

import java.util.List;
import java.util.Optional;

/**
 * Interfaz de servicio para manejar la lógica de negocio de Trainers.
 */
public interface TrainerService {
    TrainerDTO create(AuthRequest request);
    Optional<Trainer> getTrainerWithPokemonsAndTypes(String username);
    Optional<Trainer> findTrainerByUsername(String username);
    Optional<Trainer> update(Trainer trainer);
    List<PokemonDTO> getPokedex(String userame);
    TrainerDTO updateTrainerNameAndUsername(Trainer trainer, UpdateNameAndUsernameRequest request);
    public TrainerDTO updateCoinsAndLevel(Trainer trainer, UpdateCoinsRequest request);
    PokemonDTO addPokemonToTrainer(Trainer trainer, Pokemon pokemon);
    void removePokemonFromTrainer(Trainer trainer, Long pokemonId);
}
