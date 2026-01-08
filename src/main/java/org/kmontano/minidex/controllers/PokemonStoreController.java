package org.kmontano.minidex.controllers;

import org.kmontano.minidex.application.service.PokemonStoreService;
import org.kmontano.minidex.auth.AuthUtils;
import org.kmontano.minidex.domain.trainer.Trainer;
import org.kmontano.minidex.dto.response.PokemonStoreDTO;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/shop")
@CrossOrigin("${frontend.url}")
public class PokemonStoreController {
    private PokemonStoreService pokemonStoreService;

    public PokemonStoreController(PokemonStoreService pokemonStoreService) {
        this.pokemonStoreService = pokemonStoreService;
    }

    @GetMapping
    ResponseEntity<PokemonStoreDTO> getPokemonStore(Authentication authentication){
        Trainer trainer = AuthUtils.getAuthenticatedTrainer(authentication);

        return ResponseEntity.ok(pokemonStoreService.getDailyStore(trainer));
    }

    @PatchMapping("/buySpecialPokemon")
    ResponseEntity<Void> buySpecialPokemon(Authentication authentication){
        Trainer trainer = AuthUtils.getAuthenticatedTrainer(authentication);

        pokemonStoreService.buySpecialPokemon(trainer);

        return new ResponseEntity<>(HttpStatus.OK);
    }

    @PatchMapping("/buyEnvelope")
    ResponseEntity<Void> buyEnvelope(Authentication authentication){
        Trainer trainer = AuthUtils.getAuthenticatedTrainer(authentication);

        pokemonStoreService.buyBooster(trainer);

        return new ResponseEntity<>(HttpStatus.OK);
    }
}
