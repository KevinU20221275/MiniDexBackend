package org.kmontano.minidex.controllers;

import jakarta.validation.Valid;
import org.kmontano.minidex.domain.pokedex.Pokedex;
import org.kmontano.minidex.dto.response.PackPokemon;
import org.kmontano.minidex.domain.trainer.Trainer;
import org.kmontano.minidex.domain.pokemon.Pokemon;
import org.kmontano.minidex.dto.response.PokedexDTO;
import org.kmontano.minidex.dto.response.PokemonTeamDTO;
import org.kmontano.minidex.dto.request.PokemonTeamRequest;
import org.kmontano.minidex.factory.PokemonFactory;
import org.kmontano.minidex.infrastructure.mapper.PokemonResponse;
import org.kmontano.minidex.application.service.PokedexService;
import org.kmontano.minidex.infrastructure.api.PokemonApiClient;
import org.kmontano.minidex.auth.AuthUtils;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

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

        return pokedexService.getPokedexByOwner(trainer.getId())
                .map(PokedexDTO::new).map(ResponseEntity::ok)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Pokedex not found"
                ));
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

        Pokedex pokedex = pokedexService.getPokedexByOwner(trainer.getId())
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Trainer's Pokedex not found"
                ));

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

        return pokedexService.addPokemon(trainer.getId(), pokemonToSave)
                .map(PokedexDTO::new).map(ResponseEntity::ok)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Failed to add Pokemon"
                ));

    }

    /**
     * Evolves a Pokémon in the trainer's Pokedex.
     *
     * @param pokemonId       Pokémon identifier
     * @param authentication  current authentication context
     * @return updated Pokedex
     */
    @PatchMapping("/evolve/{pokemonId}")
    public ResponseEntity<PokedexDTO> evolPokemon(@PathVariable String pokemonId, Authentication authentication){
        Trainer trainer = AuthUtils.getAuthenticatedTrainer(authentication);

        return pokedexService.evolPokemon(trainer.getId(), pokemonId)
                .map(PokedexDTO::new).map(ResponseEntity::ok)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Pokemon not found"
                ));
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
     * @return HTTP 204 if successful
     */
    @DeleteMapping("/{pokemonId}")
    public ResponseEntity<Void> removePokemon(@PathVariable String pokemonId, Authentication authentication){
        Trainer trainer = AuthUtils.getAuthenticatedTrainer(authentication);
        pokedexService.removePokemon(trainer.getId(), pokemonId);

        return ResponseEntity.noContent().build();
    }

    /**
     * Removes a Pokémon from the trainer's active team.
     *
     * @param pokemonId       Pokémon identifier
     * @param authentication  current authentication context
     * @return HTTP 204 if successful
     */
    @DeleteMapping("/removePokemonToTeam/{pokemonId}")
    public ResponseEntity<Void> removePokemonFromTeam(@PathVariable String pokemonId, Authentication authentication){
        Trainer trainer = AuthUtils.getAuthenticatedTrainer(authentication);
        pokedexService.removePokemonFromTeam(trainer.getId(), pokemonId);

        return ResponseEntity.noContent().build();
    }
}
