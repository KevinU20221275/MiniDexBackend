package org.kmontano.minidex.controllers;

import jakarta.validation.Valid;
import org.kmontano.minidex.domain.pokedex.Pokedex;
import org.kmontano.minidex.domain.trainer.DailyPackStatus;
import org.kmontano.minidex.domain.trainer.Trainer;
import org.kmontano.minidex.application.service.PokedexService;
import org.kmontano.minidex.application.service.TrainerService;
import org.kmontano.minidex.auth.AuthUtils;
import org.kmontano.minidex.auth.JwtUtil;
import org.kmontano.minidex.dto.request.UpdateCoinsRequest;
import org.kmontano.minidex.dto.request.UpdateNameAndUsernameRequest;
import org.kmontano.minidex.dto.response.AuthResponse;
import org.kmontano.minidex.dto.response.PackPokemon;
import org.kmontano.minidex.dto.response.PokedexDTO;
import org.kmontano.minidex.dto.response.TrainerDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

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
    private final PokedexService pokedexService;
    private final JwtUtil jwtUtil;
    private final Logger log = LoggerFactory.getLogger(TrainerController.class);

    public TrainerController(TrainerService trainerService, PokedexService pokedexService, JwtUtil jwtUtil) {
        this.trainerService = trainerService;
        this.pokedexService = pokedexService;
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

        return ResponseEntity.ok(new TrainerDTO(trainer));
    }

    /**
     *  Endpoint que devuelve una lista de pokemons
     *
     * @param authentication auth
     * @return List<PokemonDTO>
     */
    @GetMapping("/me/pokedex")
    public ResponseEntity<PokedexDTO> getPokedex(Authentication authentication){
        Trainer trainer = AuthUtils.getAuthenticatedTrainer(authentication);
        Optional<Pokedex> pokedex = pokedexService.getPokedexByOwner(trainer.getId());

        return pokedex.map(PokedexDTO::new).map(ResponseEntity::ok).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
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

    @GetMapping("/me/envelope")
    public ResponseEntity<DailyPackStatus> getEnvelopes(Authentication authentication){
        Trainer trainer = AuthUtils.getAuthenticatedTrainer(authentication);

        DailyPackStatus packs = trainer.getDailyPack();
        log.info("Daily Packs ${}", packs);
        System.out.println("packs: ");
        System.out.println(packs);
        return ResponseEntity.ok(packs);
    }

    @PatchMapping("/me/envelope/open")
    public ResponseEntity<List<PackPokemon>> openEnvelope(Authentication authentication){
        Trainer trainer = AuthUtils.getAuthenticatedTrainer(authentication);

        return ResponseEntity.ok(trainerService.openEnvelope(trainer));
    }
}
