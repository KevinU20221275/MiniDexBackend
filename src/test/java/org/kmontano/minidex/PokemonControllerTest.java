package org.kmontano.minidex;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.MediaType;
import org.kmontano.minidex.auth.JwtFilter;
import org.kmontano.minidex.controllers.PokemonController;
import org.kmontano.minidex.dto.PokemonDTO;
import org.kmontano.minidex.models.Pokemon;
import org.kmontano.minidex.services.PokemonService;
import org.kmontano.minidex.services.TrainerService;
import org.kmontano.minidex.utils.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Pruebas unitarias del controlador de Pokémon.
 * Se verifica que los endpoints respondan correctamente con MockMvc.
 */
@WebMvcTest(PokemonController.class)
@AutoConfigureMockMvc(addFilters = false)
public class PokemonControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private PokemonService pokemonService;

    @MockitoBean
    private JwtFilter jwtFilter;

    @MockitoBean
    private TrainerService trainerService;

    @MockitoBean
    private JwtUtil jwtUtil;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @DisplayName("POST /pokemons crea un nuevo Pokémon")
    void testCreatePokemon() throws Exception {
        // Pokémon de ejemplo
        Pokemon pokemon = new Pokemon();
        pokemon.setName("Charmander");
        pokemon.setImage("charmander.png");
        pokemon.setAttack(52);
        pokemon.setDefense(43);
        pokemon.setHp(39);
        pokemon.setSpeed(65);

        // Mockea el servicio para que devuelva el pokemon creado
        when(pokemonService.save(any(Pokemon.class))).thenReturn(pokemon);

        // Realiza el GET al endpoint
        mockMvc.perform(post("/pokemons")
                        .contentType(String.valueOf(MediaType.APPLICATION_JSON))
                        .content(objectMapper.writeValueAsString(pokemon)))
                .andExpect(status().isCreated()) // Espera codigo HTTP 201 CREATED
                .andExpect(jsonPath("$.name").value("Charmander")); // Verifica que el nombre sea correcto
    }

    @Test
    @DisplayName("GET /pokemons devuelve la lista de Pokémon")
    void testGetAllPokemons() throws Exception {
        // Pokémon de ejemplo
        Pokemon p = new Pokemon();
        p.setName("Squirtle");
        p.setImage("url");
        p.setAttack(48);
        p.setDefense(65);
        p.setHp(44);
        p.setSpeed(43);

        // Mockea el servicio para que devuelva una lista con nuestro Pokémon como DTO
        when(pokemonService.findAll()).thenReturn(List.of(new PokemonDTO(p)));

        mockMvc.perform(get("/pokemons"))
                .andExpect(status().isOk())  // Espera código HTTP 200 OK
                .andExpect(jsonPath("$[0].name").value("Squirtle")); // Verifica que el primer Pokémon sea "Squirtle"
    }
}
