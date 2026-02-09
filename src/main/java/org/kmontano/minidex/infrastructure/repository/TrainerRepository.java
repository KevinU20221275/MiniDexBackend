package org.kmontano.minidex.infrastructure.repository;

import org.kmontano.minidex.domain.trainer.Trainer;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Repositorio JPA para la entidad Trainer.
 * Incluye consultas personalizadas para cargar entrenadores junto con sus pokémons.
 */
@Repository
public interface TrainerRepository extends MongoRepository<Trainer, String> {
    Optional<Trainer> findByUsername(String username);
}
