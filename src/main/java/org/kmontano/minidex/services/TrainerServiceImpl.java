package org.kmontano.minidex.services;

import org.kmontano.minidex.dto.*;
import org.kmontano.minidex.models.Pokemon;
import org.kmontano.minidex.models.Trainer;
import org.kmontano.minidex.repositories.TrainerRepository;
import org.kmontano.minidex.utils.PasswordEncoder;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Implementación de TrainerService.
 * Contiene la lógica de negocio para crear, actualizar y manipular entrenadores y sus pokémons.
 */
@Service
public class TrainerServiceImpl implements TrainerService {
    private final TrainerRepository repository;
    private final PokemonService pokemonService;

    public TrainerServiceImpl(TrainerRepository repository, PokemonService pokemonService) {
        this.repository = repository;
        this.pokemonService = pokemonService;
    }

    @Override
    public TrainerDTO create(AuthRequest request) {
        if (repository.findTrainerByUsername(request.getUsername()).isPresent()) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "El nombre de usuario ya existe");
        }

        String hashedPassword = PasswordEncoder.encodePassword(request.getPassword());

        Trainer newTrainer = new Trainer();
        newTrainer.setName(request.getName());
        newTrainer.setUsername(request.getUsername());
        newTrainer.setPassword(hashedPassword);
        newTrainer.setLevel(1);
        newTrainer.setCoins(250);

        repository.save(newTrainer);

        return new TrainerDTO(newTrainer);
    }

    @Override
    public Optional<Trainer> getTrainerWithPokemonsAndTypes(String username) {
        Optional<Trainer> trainerOptional = repository.findByUsernameWithPokemons(username);

        if (trainerOptional.isEmpty()) {
            return Optional.empty();
        }

        Trainer trainer = trainerOptional.get();

        // Recolecta los IDs de los pokémon del entrenador
        List<Long> pokemonIds = trainer.getPokemons()
                .stream()
                .map(Pokemon::getId)
                .collect(Collectors.toList());

        if (pokemonIds.isEmpty()) {
            return Optional.of(trainer);
        }

        // Obtiene todos los pokémon con sus tipos en una sola consulta
        List<Pokemon> pokemonsWithTypes = pokemonService.findAllByIdWithTypes(pokemonIds);

        // Asigna los tipos correctamente
        Map<Long, Pokemon> pokemonMap = pokemonsWithTypes.stream()
                .collect(Collectors.toMap(Pokemon::getId, p -> p));

        trainer.getPokemons().forEach(pokemon -> {
            Pokemon fullPokemon = pokemonMap.get(pokemon.getId());
            if (fullPokemon != null) {
                pokemon.setTypes(fullPokemon.getTypes());
            }
        });

        return Optional.of(trainer);
    }

    @Override
    public Optional<Trainer> findTrainerByUsername(String username){
        return repository.findTrainerByUsername(username);
    }

    @Override
    public Optional<Trainer> update(Trainer trainer) {
        return Optional.of(repository.save(trainer));
    }


    @Override
    public List<PokemonDTO> getPokedex(String username){
        return getTrainerWithPokemonsAndTypes(username)
                .map(trainer -> trainer.getPokemons()
                        .stream()
                        .map(PokemonDTO::new)
                        .collect(Collectors.toList()))
                .orElse(Collections.emptyList());
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
    public PokemonDTO addPokemonToTrainer(Trainer trainer, Pokemon pokemon) {
        // Guardar o recuperar el Pokémon de la DB según nombre
        // Buscar Pokémon por nombre o guardarlo
        Pokemon pokemonDb = pokemonService.save(pokemon);

        // Traer trainer administrado por Hibernate
        Trainer managedTrainer = repository.findById(trainer.getId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Trainer no encontrado"));

        boolean alreadyHas = managedTrainer.getPokemons()
                .stream()
                .anyMatch(p -> p.getId().equals(pokemonDb.getId()));

        if (alreadyHas) {
            // Si ya lo tiene, suma monedas según su ataque
            managedTrainer.setCoins(managedTrainer.getCoins() + pokemonDb.getAttack());
        } else {
            // Si no lo tiene, lo agrega a su pokédex
            managedTrainer.getPokemons().add(pokemonDb);
        }

        repository.save(managedTrainer);

        return new PokemonDTO(pokemonDb);
    }

    @Override
    public void removePokemonFromTrainer(Trainer trainer, Long pokemonId) {
        // Traer el trainer administrado por Hibernate
        Trainer managedTrainer = repository.findById(trainer.getId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Trainer no encontrado"));

        // Inicializar la colección si es null
        if (managedTrainer.getPokemons() == null) {
            managedTrainer.setPokemons(new ArrayList<>());
        }

        boolean removed = managedTrainer.getPokemons().removeIf(p -> p.getId().equals(pokemonId));

        if (!removed) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "El Pokémon no está en la Pokédex del entrenador");
        }
        repository.save(managedTrainer);
    }
}
