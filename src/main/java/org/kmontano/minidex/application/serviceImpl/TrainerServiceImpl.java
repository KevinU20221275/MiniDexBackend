package org.kmontano.minidex.application.serviceImpl;

import org.kmontano.minidex.application.service.DailyPackService;
import org.kmontano.minidex.application.service.PokedexService;
import org.kmontano.minidex.application.service.TrainerService;
import org.kmontano.minidex.domain.pokedex.Pokedex;
import org.kmontano.minidex.domain.trainer.DailyPackStatus;
import org.kmontano.minidex.domain.trainer.Trainer;
import org.kmontano.minidex.dto.request.AuthRequest;
import org.kmontano.minidex.dto.request.UpdateNameAndUsernameRequest;
import org.kmontano.minidex.dto.response.PackPokemon;
import org.kmontano.minidex.dto.response.TrainerDTO;
import org.kmontano.minidex.infrastructure.repository.TrainerRepository;
import org.kmontano.minidex.utils.PasswordEncoder;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.*;

/**
 * TrainerService implementation.
 *
 * This service contains the core business logic related to Trainers:
 * - Trainer creation
 * - Trainer updates
 * - Envelope (daily pack) handling
 * - Coordination with Pokedex and DailyPack services
 *
 * It acts as the application layer between controllers and domain/repositories.
 */
@Service
public class TrainerServiceImpl implements TrainerService {
    private final TrainerRepository repository;
    private final PokedexService pokedexService;
    private final DailyPackService dailyPackService;

    public TrainerServiceImpl(TrainerRepository repository, PokedexService pokedexService, DailyPackService dailyPackService) {
        this.repository = repository;
        this.pokedexService = pokedexService;
        this.dailyPackService = dailyPackService;
    }

    /**
     * Creates a new Trainer.
     *
     * Responsibilities:
     * - Validate username uniqueness
     * - Hash the password
     * - Initialize domain defaults (daily pack status)
     * - Persist the trainer
     * - Create an empty Pokedex for the trainer
     *
     * @param request authentication and registration data
     * @return TrainerDTO created trainer
     */
    @Override
    public TrainerDTO create(AuthRequest request) {
        if (repository.findByUsername(request.getUsername()).isPresent()) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Username already exists");
        }

        String hashedPassword = PasswordEncoder.encodePassword(request.getPassword());

        DailyPackStatus dailyPackStatus = new DailyPackStatus();

        Trainer newTrainer = new Trainer.Builder()
                .name(request.getName())
                .username(request.getUsername())
                .password(hashedPassword)
                .dailyPack(dailyPackStatus)
                .build();

        Trainer savedTrainer = repository.save(newTrainer);

        // Initialize trainer's pokedex
        Pokedex pokedex = new Pokedex(savedTrainer.getId());
        pokedexService.update(pokedex);

        return new TrainerDTO(newTrainer);
    }

    /**
     * Retrieves a trainer by username.
     *
     * @param username trainer username
     * @return Optional Trainer
     */
    @Override
    public Optional<Trainer> findTrainerByUsername(String username){
        return repository.findByUsername(username);
    }

    /**
     * Updates and persists a Trainer entity.
     *
     * @param trainer trainer to update
     * @return updated Trainer
     */
    @Override
    public Optional<Trainer> update(Trainer trainer) {
        return Optional.of(repository.save(trainer));
    }

    /**
     * Updates trainer name and username.
     *
     * Business rules:
     * - Username must be unique
     * - Trainer can only update its own data
     *
     * @param trainer authenticated trainer
     * @param request new name and username
     * @return updated TrainerDTO
     */
    @Override
    public TrainerDTO updateTrainerNameAndUsername(Trainer trainer, UpdateNameAndUsernameRequest request){
        Optional<Trainer> existingTrainer = findTrainerByUsername(request.getUsername());

        // Username already exists and belongs to another trainer
        if (existingTrainer.isPresent() && !existingTrainer.get().getId().equals(trainer.getId())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Username already exists");
        }

        trainer.updateNameAndUsername(request.getName(), request.getUsername());

        Trainer updatedTrainer = repository.save(trainer);
        return new TrainerDTO(updatedTrainer);
    }

    /**
     * Opens the daily envelope for a trainer.
     *
     * Flow:
     * - Validate envelope availability (domain logic)
     * - Generate daily pack pokemons
     * - Add pokemons to the trainer's pokedex
     * - Persist trainer state
     *
     * This operation is transactional to ensure consistency.
     *
     * @param trainer authenticated trainer
     * @return list of obtained PackPokemon
     */
    @Override
    @Transactional
    public List<PackPokemon> openEnvelope(Trainer trainer) {
        trainer.getDailyPack().onOpenEnvelope();

        List<PackPokemon> pokemons = dailyPackService.generateDailyPackPokemons();

        pokedexService.addPokemonsFromEnvelope(pokemons, trainer.getId());
        repository.save(trainer);

        return pokemons;
    }
}
