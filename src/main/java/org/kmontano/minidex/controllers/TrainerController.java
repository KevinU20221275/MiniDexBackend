package org.kmontano.minidex.controllers;

import jakarta.validation.Valid;
import org.kmontano.minidex.domain.pokedex.Pokedex;
import org.kmontano.minidex.domain.trainer.DailyPackStatus;
import org.kmontano.minidex.domain.trainer.Trainer;
import org.kmontano.minidex.application.service.PokedexService;
import org.kmontano.minidex.application.service.TrainerService;
import org.kmontano.minidex.auth.AuthUtils;
import org.kmontano.minidex.auth.JwtUtil;
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
 * REST controller for Trainer-related operations.
 *
 * Exposes endpoints to:
 * - Retrieve authenticated trainer data
 * - Access trainer pokedex
 * - Update trainer profile
 * - Manage daily envelopes
 *
 * All endpoints require authentication.
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
     * Returns the authenticated trainer information.
     *
     * @param authentication Spring Security authentication
     * @return TrainerDTO
     */
    @GetMapping("/me")
    public ResponseEntity<TrainerDTO> getTrainer(Authentication authentication) {
        Trainer trainer = AuthUtils.getAuthenticatedTrainer(authentication);

        return ResponseEntity.ok(new TrainerDTO(trainer));
    }

    /**
     * Returns the authenticated trainer's pokedex.
     *
     * @param authentication Spring Security authentication
     * @return PokedexDTO
     */
    @GetMapping("/me/pokedex")
    public ResponseEntity<PokedexDTO> getPokedex(Authentication authentication){
        Trainer trainer = AuthUtils.getAuthenticatedTrainer(authentication);
        Optional<Pokedex> pokedex = pokedexService.getPokedexByOwner(trainer.getId());

        return pokedex.map(PokedexDTO::new).map(ResponseEntity::ok).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Pokedex not found for trainer"));
    }

    /**
     * Updates the trainer's name and username.
     *
     * After updating the username, a new JWT token is generated
     * to reflect the new identity.
     *
     * @param request update request
     * @param authentication Spring Security authentication
     * @return AuthResponse containing new token and trainer data
     */
    @PutMapping("/me")
    public ResponseEntity<AuthResponse> updateNameUsername(@Valid @RequestBody UpdateNameAndUsernameRequest request, Authentication authentication){
        Trainer trainer = AuthUtils.getAuthenticatedTrainer(authentication);

        TrainerDTO updatedTrainer = trainerService.updateTrainerNameAndUsername(trainer, request);

        String token = jwtUtil.generateToken(updatedTrainer.getUsername());

        return ResponseEntity.ok(new AuthResponse(token, updatedTrainer));
    }

    /**
     * Returns the current daily envelope status for the trainer.
     *
     * @param authentication Spring Security authentication
     * @return DailyPackStatus
     */
    @GetMapping("/me/envelope")
    public ResponseEntity<DailyPackStatus> getEnvelopes(Authentication authentication){
        Trainer trainer = AuthUtils.getAuthenticatedTrainer(authentication);

        DailyPackStatus packs = trainer.getDailyPack();

        return ResponseEntity.ok(packs);
    }

    /**
     * Opens the daily envelope and returns the obtained pokemons.
     *
     * @param authentication Spring Security authentication
     * @return list of PackPokemon
     */
    @PatchMapping("/me/envelope/open")
    public ResponseEntity<List<PackPokemon>> openEnvelope(Authentication authentication){
        Trainer trainer = AuthUtils.getAuthenticatedTrainer(authentication);

        return ResponseEntity.ok(trainerService.openEnvelope(trainer));
    }
}
