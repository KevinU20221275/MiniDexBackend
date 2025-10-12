package org.kmontano.minidex.controllers;

import jakarta.validation.Valid;
import org.kmontano.minidex.dto.PokemonDTO;
import org.kmontano.minidex.models.Pokemon;
import org.kmontano.minidex.services.PokemonService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Controlador REST para gestión de Pokémons.
 * Permite listar y crear nuevos Pokémons.
 */
@RestController
@RequestMapping("pokemons")
@CrossOrigin("${frontend.url}")
public class PokemonController {
    private final Logger log = LoggerFactory.getLogger(PokemonController.class);
    private final PokemonService pokemonService;

    public PokemonController(PokemonService pokemonService) {
        this.pokemonService = pokemonService;
    }

    /**
     * Obtener todos los Pokémons.
     */
    @GetMapping
    public ResponseEntity<List<PokemonDTO>> getAllPokemons() {
        return ResponseEntity.ok(pokemonService.findAll());
    }

    /**
     * Endpoint para crear un nuevo pokemon
     *
     * @param pokemon
     * @return un pokemonDTO
     */
    @PostMapping
    public ResponseEntity<PokemonDTO> createPokemon(@Valid @RequestBody Pokemon pokemon) {
        Pokemon savedPokemon = pokemonService.save(pokemon);
        log.info("Nuevo Pokémon creado: {}", savedPokemon.getName());
        return ResponseEntity.status(HttpStatus.CREATED).body(new PokemonDTO(savedPokemon));
    }
}
