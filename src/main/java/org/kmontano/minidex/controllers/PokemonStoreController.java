package org.kmontano.minidex.controllers;

import org.kmontano.minidex.application.service.PokemonStoreService;
import org.kmontano.minidex.auth.AuthUtils;
import org.kmontano.minidex.domain.trainer.Trainer;
import org.kmontano.minidex.dto.response.PokemonStoreDTO;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

/**
 * REST controller responsible for the Pokémon daily shop.
 *
 * Exposes endpoints to:
 * - Retrieve the daily store
 * - Purchase booster packs
 * - Purchase the daily special Pokémon
 */

@RestController
@RequestMapping("/api/v1/shop")
@CrossOrigin("${frontend.url}")
public class PokemonStoreController {
    private final PokemonStoreService pokemonStoreService;

    public PokemonStoreController(PokemonStoreService pokemonStoreService) {
        this.pokemonStoreService = pokemonStoreService;
    }

    /**
     * Returns the daily Pokémon shop for the authenticated trainer.
     *
     * @param authentication Spring Security authentication context
     * @return daily shop data including prices and availability
     */
    @GetMapping
    ResponseEntity<PokemonStoreDTO> getPokemonStore(Authentication authentication){
        Trainer trainer = AuthUtils.getAuthenticatedTrainer(authentication);

        return ResponseEntity.ok(pokemonStoreService.getDailyStore(trainer));
    }
}
