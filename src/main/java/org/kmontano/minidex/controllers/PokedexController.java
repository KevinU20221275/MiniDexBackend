package org.kmontano.minidex.controllers;

import jakarta.validation.Valid;
import org.kmontano.minidex.domain.pokedex.Pokedex;
import org.kmontano.minidex.dto.response.*;
import org.kmontano.minidex.domain.trainer.Trainer;
import org.kmontano.minidex.domain.pokemon.Pokemon;
import org.kmontano.minidex.dto.request.PokemonTeamRequest;
import org.kmontano.minidex.factory.PokemonFactory;
import org.kmontano.minidex.infrastructure.mapper.PokemonResponse;
import org.kmontano.minidex.application.service.PokedexService;
import org.kmontano.minidex.infrastructure.api.PokemonApiClient;
import org.kmontano.minidex.auth.AuthUtils;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * REST controller responsible for managing a Trainer's Pokedex.
 *
 * Provides endpoints to:
 *
 *  - Retrieve the trainer's Pokedex
 *  - Manage captured Pokémon
 *  - Manage the active Pokémon team
 *  - Evolve Pokémon
 *
 * All endpoints require authentication and operate on the currently
 * authenticated trainer.
 */
@RestController
@RequestMapping("pokedex")
@CrossOrigin("${frontend.url}")
public class PokedexController {
    private final PokedexService pokedexService;
    private final PokemonApiClient pokemonApiClient;
    private final PokemonFactory pokemonFactory;

    public PokedexController(PokedexService pokedexService, PokemonApiClient pokemonApiClient, PokemonFactory pokemonFactory) {
        this.pokedexService = pokedexService;
        this.pokemonApiClient = pokemonApiClient;
        this.pokemonFactory = pokemonFactory;
    }

    /**
     * Retrieves the authenticated trainer's Pokedex.
     *
     * @param authentication current authentication context
     * @return the trainer's Pokedex
     */
    @GetMapping
    public ResponseEntity<PokedexDTO> getPokedex(Authentication authentication) {
        Trainer trainer = AuthUtils.getAuthenticatedTrainer(authentication);

        Pokedex pokedex = pokedexService.getPokedexByOwner(trainer.getId());
        return ResponseEntity.ok(new PokedexDTO(pokedex));
    }

    /**
     * Retrieves the expanded Pokémon team of the authenticated trainer.
     *
     * @param authentication current authentication context
     * @return the active Pokémon team
     */
    @GetMapping("/getPokemonTeam")
    public ResponseEntity<PokemonTeamDTO> getPokemonTeam(Authentication authentication){
        Trainer trainer = AuthUtils.getAuthenticatedTrainer(authentication);

        Pokedex pokedex = pokedexService.getPokedexByOwner(trainer.getId());

        List<Pokemon> team = pokedex.getPokemonTeamExpanded();

        return ResponseEntity.ok(new PokemonTeamDTO(team));
    }

    /**
     * Adds a Pokémon to the trainer's Pokedex.
     *
     * The Pokémon is fetched from the external Pokémon API and
     * converted into a full domain Pokémon before being stored.
     *
     * @param pokemon         basic Pokémon data (name and shiny flag)
     * @param authentication  current authentication context
     * @return updated Pokedex
     */
    @PutMapping
    public ResponseEntity<PokedexDTO> addPokemon(@Valid @RequestBody PackPokemon pokemon, Authentication authentication) {
        Trainer trainer = AuthUtils.getAuthenticatedTrainer(authentication);
        PokemonResponse pokemonResponse = pokemonApiClient.getPokemonByName(pokemon.getName());
        Pokemon pokemonToSave = pokemonFactory.toFullPokemon(pokemonResponse, pokemon.isShiny());

        Pokedex pokedex = pokedexService.addPokemon(trainer.getId(), pokemonToSave);

        return ResponseEntity.ok(new PokedexDTO(pokedex));
    }

    /**
     * Evolves a Pokémon in the trainer's Pokedex.
     *
     * @param pokemonId       Pokémon identifier
     * @param authentication  current authentication context
     * @return updated Pokemon
     */
    @PatchMapping("/evolve/{pokemonId}")
    public ResponseEntity<EvolPokemonResponse> evolPokemon(@PathVariable String pokemonId, Authentication authentication){
        Trainer trainer = AuthUtils.getAuthenticatedTrainer(authentication);

        Pokemon pokemon = pokedexService.evolPokemon(trainer, pokemonId);

        return ResponseEntity.ok(new EvolPokemonResponse(trainer.getCoins(), trainer.getXp(), trainer.getLevel(), pokemon));
    }

    /**
     * Adds a Pokémon to the trainer's active team.
     *
     * @param request         request containing the Pokémon identifier
     * @param authentication  current authentication context
     * @return HTTP 200 if successful
     */
    @PatchMapping("/addPokemonToTeam")
    public ResponseEntity<Void> addPokemonTeam(@RequestBody PokemonTeamRequest request, Authentication authentication) {
        Trainer trainer = AuthUtils.getAuthenticatedTrainer(authentication);

        pokedexService.addPokemonToTeam(trainer.getId(), request.getPokemonId());

        return ResponseEntity.ok().build();
    }

    /**
     * Removes a Pokémon from the trainer's Pokedex.
     *
     * @param pokemonId       Pokémon identifier
     * @param authentication  current authentication context
     * @return TransferPokemonResponse if successful
     */
    @DeleteMapping("/{pokemonId}")
    public ResponseEntity<TransferPokemonResponse> removePokemon(@PathVariable String pokemonId, Authentication authentication){
        Trainer trainer = AuthUtils.getAuthenticatedTrainer(authentication);
        pokedexService.removePokemon(trainer, pokemonId);

        return ResponseEntity.ok(new TransferPokemonResponse(trainer.getLevel(), trainer.getXp(), trainer.getCoins()));
    }

    /**
     * Removes a Pokémon from the trainer's active team.
     *
     * @param pokemonId       Pokémon identifier
     * @param authentication  current authentication context
     * @return HTTP 204 if successful
     */
    @DeleteMapping("/removePokemonFromTeam/{pokemonId}")
    public ResponseEntity<Void> removePokemonFromTeam(@PathVariable String pokemonId, Authentication authentication){
        Trainer trainer = AuthUtils.getAuthenticatedTrainer(authentication);
        pokedexService.removePokemonFromTeam(trainer.getId(), pokemonId);

        return ResponseEntity.noContent().build();
    }
}
