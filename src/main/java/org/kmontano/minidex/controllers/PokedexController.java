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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

/**
 * Controlador REST para gestión de Pokémons.
 * Permite listar y crear nuevos Pokémons.
 */
@RestController
@RequestMapping("pokedex")
@CrossOrigin("${frontend.url}")
public class PokedexController {
    private final Logger log = LoggerFactory.getLogger(PokedexController.class);
    private final PokedexService pokedexService;
    private final PokemonApiClient pokemonApiClient;
    private final PokemonFactory pokemonFactory;

    public PokedexController(PokedexService pokedexService, PokemonApiClient pokemonApiClient, PokemonFactory pokemonFactory) {
        this.pokedexService = pokedexService;
        this.pokemonApiClient = pokemonApiClient;
        this.pokemonFactory = pokemonFactory;
    }

    /**
     * Obtener la pokedex.
     */
    @GetMapping
    public ResponseEntity<PokedexDTO> getPokedex(Authentication authentication) {
        Trainer trainer = AuthUtils.getAuthenticatedTrainer(authentication);

        return pokedexService.getPokedexByOwner(trainer.getId())
                .map(PokedexDTO::new).map(ResponseEntity::ok)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Pokedex no encontrada"
                ));
    }

    @GetMapping("/getPokemonTeam")
    public ResponseEntity<PokemonTeamDTO> getPokemonTeam(Authentication authentication){
        Trainer trainer = AuthUtils.getAuthenticatedTrainer(authentication);

        Pokedex pokedex = pokedexService.getPokedexByOwner(trainer.getId())
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "No se encontro la pokedex del entrenador"
                ));

        List<Pokemon> team = pokedex.getPokemonTeamExpanded();

        return ResponseEntity.ok(new PokemonTeamDTO(team));
    }

    /**
     * Endpoint para crear un nuevo pokemon
     *
     * @param pokemon
     * @return un Pokedex
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
                        "Fallo al agregar el pokemon"
                ));

    }

    @PatchMapping("/evolve/{pokemonId}")
    public ResponseEntity<PokedexDTO> evolPokemon(@PathVariable String pokemonId, Authentication authentication){
        Trainer trainer = AuthUtils.getAuthenticatedTrainer(authentication);

        return pokedexService.evolPokemon(trainer.getId(), pokemonId)
                .map(PokedexDTO::new).map(ResponseEntity::ok)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "No se encontro el pokemon"
                ));
    }

    @PatchMapping("/addPokemonToTeam")
    public ResponseEntity<Void> addPokemonTeam(@RequestBody PokemonTeamRequest request, Authentication authentication) {
        Trainer trainer = AuthUtils.getAuthenticatedTrainer(authentication);

        pokedexService.addPokemonToTeam(trainer.getId(), request.getPokemonId());

        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{pokemonId}")
    public ResponseEntity<Void> removePokemon(@PathVariable String pokemonId, Authentication authentication){
        Trainer trainer = AuthUtils.getAuthenticatedTrainer(authentication);
        pokedexService.removePokemon(trainer.getId(), pokemonId);

        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/removePokemonToTeam/{pokemonId}")
    public ResponseEntity<Void> removePokemonFromTeam(@PathVariable String pokemonId, Authentication authentication){
        Trainer trainer = AuthUtils.getAuthenticatedTrainer(authentication);
        pokedexService.removePokemonFromTeam(trainer.getId(), pokemonId);

        return ResponseEntity.noContent().build();
    }
}
