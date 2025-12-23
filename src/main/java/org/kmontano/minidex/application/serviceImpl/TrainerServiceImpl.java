package org.kmontano.minidex.application.serviceImpl;

import org.kmontano.minidex.application.service.TrainerService;
import org.kmontano.minidex.domain.pokedex.Pokedex;
import org.kmontano.minidex.domain.trainer.DailyPackStatus;
import org.kmontano.minidex.domain.trainer.Trainer;
import org.kmontano.minidex.dto.request.AuthRequest;
import org.kmontano.minidex.dto.request.UpdateCoinsRequest;
import org.kmontano.minidex.dto.request.UpdateNameAndUsernameRequest;
import org.kmontano.minidex.dto.response.TrainerDTO;
import org.kmontano.minidex.infrastructure.repository.PokedexRepository;
import org.kmontano.minidex.infrastructure.repository.TrainerRepository;
import org.kmontano.minidex.utils.PasswordEncoder;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.*;

/**
 * Implementación de TrainerService.
 * Contiene la lógica de negocio para crear, actualizar y manipular entrenadores y sus pokémons.
 */
@Service
public class TrainerServiceImpl implements TrainerService {
    private final TrainerRepository repository;
    private final PokedexRepository pokedexRepository;
    private final DailyPackServiceImpl dailyPackServiceImpl;

    public TrainerServiceImpl(TrainerRepository repository, PokedexRepository pokedexRepository, DailyPackServiceImpl dailyPackServiceImpl) {
        this.repository = repository;
        this.pokedexRepository = pokedexRepository;
        this.dailyPackServiceImpl = dailyPackServiceImpl;
    }


    @Override
    public TrainerDTO create(AuthRequest request) {
        if (repository.findByUsername(request.getUsername()).isPresent()) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "El nombre de usuario ya existe");
        }

        String hashedPassword = PasswordEncoder.encodePassword(request.getPassword());

        Trainer newTrainer = new Trainer();
        DailyPackStatus dailyPackStatus = dailyPackServiceImpl.initialize();

        newTrainer.setName(request.getName())
                .setUsername(request.getUsername())
                .setPassword(hashedPassword)
                .setLevel(1)
                .setCoins(250)
                .setWins(0)
                .setLoses(0)
                .setDailyPack(dailyPackStatus);

        Trainer savedTrainer = repository.save(newTrainer);

        // pokedex del usuario
        Pokedex pokedex = new Pokedex();
        pokedex.setOwnerId(savedTrainer.getId());
        pokedexRepository.save(pokedex);

        return new TrainerDTO(newTrainer);
    }

    @Override
    public Optional<Trainer> findTrainerByUsername(String username){
        return repository.findByUsername(username);
    }

    @Override
    public Optional<Trainer> update(Trainer trainer) {
        return Optional.of(repository.save(trainer));
    }


    @Override
    public TrainerDTO updateTrainerNameAndUsername(Trainer trainer, UpdateNameAndUsernameRequest request){
        Optional<Trainer> existingTrainer = findTrainerByUsername(request.getUsername());

        // Si el username existe y pertenece a otro usuario, lanza una excepción
        if (existingTrainer.isPresent() && !existingTrainer.get().getId().equals(trainer.getId())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Username already exists");
        }

        trainer.setName(request.getName());
        trainer.setUsername(request.getUsername());

        Trainer updatedTrainer = repository.save(trainer);
        return new TrainerDTO(updatedTrainer);
    }

    @Override
    public TrainerDTO updateCoinsAndLevel(Trainer trainer, UpdateCoinsRequest request) {
        int coins = request.getCoins();

        switch (request.getAction()) {
            case "subtract" -> {
                if (trainer.getCoins() < coins) {
                    throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "No tienes monedas suficientes");
                }
                trainer.setCoins(trainer.getCoins() - coins);
            }
            case "add" -> trainer.setCoins(trainer.getCoins() + coins);
            default -> throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Acción no válida");
        }

        trainer.setLevel(trainer.getLevel() + 1);

        Trainer updatedTrainer = repository.save(trainer);

        return new TrainerDTO(updatedTrainer);
    }

    @Override
    public TrainerDTO openEnvelope(Trainer trainer, String envelopeId) {
        trainer.openEnvelope(envelopeId);
        return new TrainerDTO(repository.save(trainer));
    }
}
