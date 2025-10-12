package org.kmontano.minidex.controllers;

import jakarta.validation.Valid;
import org.kmontano.minidex.dto.*;
import org.kmontano.minidex.models.Pokemon;
import org.kmontano.minidex.models.Trainer;
import org.kmontano.minidex.services.TrainerService;
import org.kmontano.minidex.utils.AuthUtils;
import org.kmontano.minidex.utils.JwtUtil;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

/**
 * Controlador REST para operaciones de Trainer.
 * Permite consultar datos del entrenador, su pokédex y actualizar información.
 */
@RestController
@RequestMapping("/trainers")
@CrossOrigin("${frontend.url}")
public class TrainerController {
    private final TrainerService trainerService;
    private final JwtUtil jwtUtil;

    public TrainerController(TrainerService trainerService, JwtUtil jwtUtil) {
        this.trainerService = trainerService;
        this.jwtUtil = jwtUtil;
    }

    /**
     * Enpoint que devuelve un entrenador con sus datos completos
     *
     * @param authentication auth
     * @return TrainerDTO
     */
    @GetMapping("/me")
    public ResponseEntity<TrainerDTO> getTrainer(Authentication authentication) {
        Trainer trainer = AuthUtils.getAuthenticatedTrainer(authentication);

        return trainerService.getTrainerWithPokemonsAndTypes(trainer.getUsername())
                .map(TrainerDTO::new)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.status(HttpStatus.NOT_FOUND).build());
    }

    /**
     *  Endpoint que devuelve una lista de pokemons
     *
     * @param authentication auth
     * @return List<PokemonDTO>
     */
    @GetMapping("/me/pokedex")
    public ResponseEntity<List<PokemonDTO>> getPokedex(Authentication authentication){
        Trainer trainer = AuthUtils.getAuthenticatedTrainer(authentication);
        List<PokemonDTO> pokedex = trainerService.getPokedex(trainer.getUsername());
        return ResponseEntity.ok(pokedex);
    }

    /**
     * Endpoint que resive una UpdateNameAndUsernameRequest
     * con el nombre y el nombre de usuario del entrenador y los
     * actualiza
     *
     * retorna un TrainerDTO y un token de acceso
     *
     * @param request name and username
     * @param authentication auth
     * @return AuthResponse
     */
    @PutMapping("/me")
    public ResponseEntity<AuthResponse> updateNameUsername(@Valid @RequestBody UpdateNameAndUsernameRequest request, Authentication authentication){
        Trainer trainer = AuthUtils.getAuthenticatedTrainer(authentication);

        Optional<Trainer> trainerDB = trainerService.findTrainerByUsername(request.getUsername());

        TrainerDTO updatedTrainer = trainerService.updateTrainerNameAndUsername(trainer, request);

        // Genera un nuevo token con el nuevo nombre de usuario
        String token = jwtUtil.generateToken(updatedTrainer.getUsername());

        return ResponseEntity.ok(new AuthResponse(token, updatedTrainer));
    }

    /**
     * Endpoint que recibe un UpdateCoinsRequest con las monedas y
     * la accion a realizar
     * retorna el Trainer Actualizado
     *
     * @param request coins and action
     * @param authentication auth
     * @return TrainerDTO
     */
    @PatchMapping("/me/coins")
    public ResponseEntity<TrainerDTO> updateTrainerCoinsAndLevel(@Valid @RequestBody UpdateCoinsRequest request, Authentication authentication){
        Trainer trainer = AuthUtils.getAuthenticatedTrainer(authentication);
        TrainerDTO updatedTrainer = trainerService.updateCoinsAndLevel(trainer, request);

        return ResponseEntity.ok(updatedTrainer);
    }


    /**
     * Endpoint que recibe un pokemon y lo agrega a la pokedex del entrenador
     * retorna el pokemon agregado
     *
     * @param pokemon pokemon object
     * @param authentication auth
     * @return PokemonDTO
     */
    @PostMapping("/me/pokedex")
    public ResponseEntity<PokemonDTO> addPokemonToPokedex(@Valid @RequestBody Pokemon pokemon, Authentication authentication){
        Trainer trainer = AuthUtils.getAuthenticatedTrainer(authentication);
        PokemonDTO pokemonDTO = trainerService.addPokemonToTrainer(trainer, pokemon);

        return ResponseEntity.ok(pokemonDTO);
    }

    /**
     * Endpoint que recibe el id del pokemon a eliminar de la pokedex
     * del entrenador
     *
     * @param pokemonId Long
     * @param authentication auth
     * @return no content
     */
    @DeleteMapping("/me/pokedex/{pokemonId}")
    public ResponseEntity<?> removePokemonFromPokedex(@PathVariable Long pokemonId, Authentication authentication){
        Trainer trainer = AuthUtils.getAuthenticatedTrainer(authentication);
        trainerService.removePokemonFromTrainer(trainer, pokemonId);

        return ResponseEntity.noContent().build();
    }
}
