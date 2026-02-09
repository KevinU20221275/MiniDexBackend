package org.kmontano.minidex.controllers;

import jakarta.validation.Valid;
import org.kmontano.minidex.application.service.PokemonStoreService;
import org.kmontano.minidex.domain.pokedex.Pokedex;
import org.kmontano.minidex.domain.pokemon.Pokemon;
import org.kmontano.minidex.domain.trainer.DailyPackStatus;
import org.kmontano.minidex.domain.trainer.Trainer;
import org.kmontano.minidex.application.service.PokedexService;
import org.kmontano.minidex.application.service.TrainerService;
import org.kmontano.minidex.auth.AuthUtils;
import org.kmontano.minidex.auth.JwtUtil;
import org.kmontano.minidex.dto.request.PokemonTeamRequest;
import org.kmontano.minidex.dto.request.UpdateNameAndUsernameRequest;
import org.kmontano.minidex.dto.response.*;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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
@RequestMapping("/api/${api.version}/trainers/me")
@CrossOrigin("${frontend.url}")
public class TrainerController {
    private final TrainerService trainerService;
    private final PokedexService pokedexService;
    private final JwtUtil jwtUtil;
    private final PokemonStoreService pokemonStoreService;

    public TrainerController(TrainerService trainerService, PokedexService pokedexService, JwtUtil jwtUtil, PokemonStoreService pokemonStoreService) {
        this.trainerService = trainerService;
        this.pokedexService = pokedexService;
        this.jwtUtil = jwtUtil;
        this.pokemonStoreService = pokemonStoreService;
    }

    /**
     * Returns the authenticated trainer information.
     *
     * @param authentication Spring Security authentication
     * @return TrainerDTO
     */
    @GetMapping
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
    @GetMapping("/pokedex")
    public ResponseEntity<PokedexDTO> getPokedex(Authentication authentication){
        Trainer trainer = AuthUtils.getAuthenticatedTrainer(authentication);
        Pokedex pokedex = pokedexService.getPokedexByOwner(trainer.getId());

        return ResponseEntity.ok(new PokedexDTO(pokedex));
    }

    /**
     * Returns the current daily envelope status for the trainer.
     *
     * @param authentication Spring Security authentication
     * @return DailyPackStatus
     */
    @GetMapping("/daily-packs")
    public ResponseEntity<DailyPackStatus> getEnvelopes(Authentication authentication){
        Trainer trainer = AuthUtils.getAuthenticatedTrainer(authentication);

        DailyPackStatus packs = trainer.getDailyPack();
        packs.resetIfNeeded();

        return ResponseEntity.ok(packs);
    }

    /**
     * Retrieves the expanded Pokémon team of the authenticated trainer.
     *
     * @param authentication current authentication context
     * @return the active Pokémon team
     */
    @GetMapping("/team")
    public ResponseEntity<PokemonTeamDTO> getPokemonTeam(Authentication authentication){
        Trainer trainer = AuthUtils.getAuthenticatedTrainer(authentication);

        Pokedex pokedex = pokedexService.getPokedexByOwner(trainer.getId());

        List<Pokemon> team = pokedex.getPokemonTeamExpanded();

        return ResponseEntity.ok(new PokemonTeamDTO(team));
    }

    /**
     * Opens the daily envelope and returns the obtained pokemons.
     *
     * @param authentication Spring Security authentication
     * @return list of PackPokemon
     */
    @PostMapping("/daily-packs/open")
    public ResponseEntity<BoosterResponseDTO> openEnvelope(Authentication authentication){
        Trainer trainer = AuthUtils.getAuthenticatedTrainer(authentication);

        return ResponseEntity.ok(trainerService.openEnvelope(trainer));
    }

    /**
     * Purchases the daily special Pokémon for the authenticated trainer.
     *
     * @param authentication Spring Security authentication context
     * @return HTTP 200 if the purchase was successful
     */
    @PostMapping("/pokemon")
    ResponseEntity<BuySpecialPokemonResponseDTO> buySpecialPokemon(Authentication authentication){
        Trainer trainer = AuthUtils.getAuthenticatedTrainer(authentication);

        return ResponseEntity.ok(pokemonStoreService.buySpecialPokemon(trainer));
    }

    /**
     * Purchases a booster pack and returns the Pokémon obtained.
     *
     * @param authentication Spring Security authentication context
     * @return Pokémon obtained from the booster pack
     */
    @PostMapping("/booster-packs")
    ResponseEntity<BuyBoosterResponseDTO> buyEnvelope(Authentication authentication){
        Trainer trainer = AuthUtils.getAuthenticatedTrainer(authentication);

        return ResponseEntity.ok(pokemonStoreService.buyBooster(trainer));
    }

    /**
     * Adds a Pokémon to the trainer's active team.
     *
     * @param request         request containing the Pokémon identifier
     * @param authentication  current authentication context
     * @return HTTP 200 if successful
     */
    @PostMapping("/team")
    public ResponseEntity<Void> addPokemonTeam(@RequestBody PokemonTeamRequest request, Authentication authentication) {
        Trainer trainer = AuthUtils.getAuthenticatedTrainer(authentication);

        pokedexService.addPokemonToTeam(trainer.getId(), request.getPokemonId());

        return ResponseEntity.ok().build();
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
    @PutMapping
    public ResponseEntity<AuthResponse> updateNameUsername(@Valid @RequestBody UpdateNameAndUsernameRequest request, Authentication authentication){
        Trainer trainer = AuthUtils.getAuthenticatedTrainer(authentication);

        TrainerDTO updatedTrainer = trainerService.updateTrainerNameAndUsername(trainer, request);

        String token = jwtUtil.generateToken(updatedTrainer.getUsername());

        return ResponseEntity.ok(new AuthResponse(token, updatedTrainer));
    }

    /**
     * Evolves a Pokémon in the trainer's Pokedex.
     *
     * @param pokemonId       Pokémon identifier
     * @param authentication  current authentication context
     * @return updated Pokemon
     */
    @PostMapping("/pokemons/{pokemonId}/evolution")
    public ResponseEntity<EvolutionPokemonResponse> evolutionPokemon(@PathVariable String pokemonId, Authentication authentication){
        Trainer trainer = AuthUtils.getAuthenticatedTrainer(authentication);

        return ResponseEntity.ok(pokedexService.evolutionPokemon(trainer, pokemonId));
    }

    /**
     * Removes a Pokémon from the trainer's active team.
     *
     * @param pokemonId       Pokémon identifier
     * @param authentication  current authentication context
     * @return HTTP 204 if successful
     */
    @DeleteMapping("/team/{pokemonId}")
    public ResponseEntity<Void> removePokemonFromTeam(@PathVariable String pokemonId, Authentication authentication){
        Trainer trainer = AuthUtils.getAuthenticatedTrainer(authentication);
        pokedexService.removePokemonFromTeam(trainer.getId(), pokemonId);

        return ResponseEntity.noContent().build();
    }

    /**
     * Removes a Pokémon from the trainer's Pokedex.
     *
     * @param pokemonId       Pokémon identifier
     * @param authentication  current authentication context
     * @return TransferPokemonResponse if successful
     */
    @DeleteMapping("/pokedex/{pokemonId}")
    public ResponseEntity<TransferPokemonResponse> removePokemon(@PathVariable String pokemonId, Authentication authentication){
        Trainer trainer = AuthUtils.getAuthenticatedTrainer(authentication);

        return ResponseEntity.ok(pokedexService.removePokemon(trainer, pokemonId));
    }
}
