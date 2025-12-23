package org.kmontano.minidex.application.service;


import org.kmontano.minidex.domain.trainer.Trainer;
import org.kmontano.minidex.dto.request.AuthRequest;
import org.kmontano.minidex.dto.request.UpdateCoinsRequest;
import org.kmontano.minidex.dto.request.UpdateNameAndUsernameRequest;
import org.kmontano.minidex.dto.response.TrainerDTO;

import java.util.Optional;

/**
 * Interfaz de servicio para manejar la lógica de negocio de Trainers.
 */
public interface TrainerService {
    TrainerDTO create(AuthRequest request);
    Optional<Trainer> findTrainerByUsername(String username);
    Optional<Trainer> update(Trainer trainer);
    TrainerDTO updateTrainerNameAndUsername(Trainer trainer, UpdateNameAndUsernameRequest request);
    TrainerDTO updateCoinsAndLevel(Trainer trainer, UpdateCoinsRequest request);
    TrainerDTO openEnvelope(Trainer trainer, String envelopeId);
}
