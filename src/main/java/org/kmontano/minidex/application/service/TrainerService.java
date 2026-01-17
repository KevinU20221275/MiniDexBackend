package org.kmontano.minidex.application.service;


import org.kmontano.minidex.domain.trainer.Trainer;
import org.kmontano.minidex.dto.request.AuthRequest;
import org.kmontano.minidex.dto.request.UpdateCoinsRequest;
import org.kmontano.minidex.dto.request.UpdateNameAndUsernameRequest;
import org.kmontano.minidex.dto.response.BoosterResponseDTO;
import org.kmontano.minidex.dto.response.PackPokemon;
import org.kmontano.minidex.dto.response.TrainerDTO;

import java.util.List;
import java.util.Optional;


/**
 * Service interface to handle business logic of Trainers.
 */
public interface TrainerService {
    TrainerDTO create(AuthRequest request);
    Optional<Trainer> findTrainerByUsername(String username);
    Optional<Trainer> update(Trainer trainer);
    TrainerDTO updateTrainerNameAndUsername(Trainer trainer, UpdateNameAndUsernameRequest request);
    BoosterResponseDTO openEnvelope(Trainer trainer);
}
