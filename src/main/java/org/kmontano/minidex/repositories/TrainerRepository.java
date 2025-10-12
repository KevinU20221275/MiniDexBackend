package org.kmontano.minidex.repositories;

import org.kmontano.minidex.models.Trainer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Repositorio JPA para la entidad Trainer.
 * Incluye consultas personalizadas para cargar entrenadores junto con sus pokémons.
 */
@Repository
public interface TrainerRepository extends JpaRepository<Trainer, Long> {
    /**
     * Busca un entrenador por su username, cargando también sus pokémons asociados.
     */
    @Query("SELECT t FROM Trainer t LEFT JOIN FETCH t.pokemons WHERE t.username = :username")
    Optional<Trainer> findByUsernameWithPokemons(@Param("username") String username);

    /**
     * Busca un entrenador solo por username.
     */
    Optional<Trainer> findTrainerByUsername(String username);
}
