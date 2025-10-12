package org.kmontano.minidex;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.kmontano.minidex.controllers.TrainerController;
import org.kmontano.minidex.dto.PokemonDTO;
import org.kmontano.minidex.dto.TrainerDTO;
import org.kmontano.minidex.dto.UpdateCoinsRequest;
import org.kmontano.minidex.models.Pokemon;
import org.kmontano.minidex.models.PokemonType;
import org.kmontano.minidex.models.Trainer;
import org.kmontano.minidex.utils.JwtUtil;
import org.kmontano.minidex.services.PokemonService;
import org.kmontano.minidex.services.TrainerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.ArrayList;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(TrainerController.class)
@AutoConfigureMockMvc(addFilters = false)
class TrainerControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private TrainerService trainerService;

    @MockitoBean
    private PokemonService pokemonService;

    @MockitoBean
    private JwtUtil jwtUtil;

    @Autowired
    private ObjectMapper objectMapper;

    private Trainer trainer;

    @BeforeEach
    void setUp() {
        trainer = new Trainer();
        trainer.setId(1L);
        trainer.setUsername("ash123");
        trainer.setName("Ash");
        trainer.setCoins(100);
        trainer.setLevel(5);
        trainer.setPokemons(new ArrayList<>());
    }

    @Test
    @DisplayName("Agrega un Pokémon a la pokédex del entrenador autenticado")
    void shouldAddPokemonToTrainerPokedex() throws Exception {
        Pokemon pokemon = new Pokemon();
        pokemon.setId(1L);
        pokemon.setName("Pikachu");
        pokemon.setAttack(50);
        pokemon.setDefense(40);
        pokemon.setHp(100);
        pokemon.setSpeed(90);
        pokemon.setImage("pikachu.png");
        pokemon.setTypes(List.of(new PokemonType("electric")));

        // Mock del service: devuelve un PokemonDTO basado en el Pokémon recibido
        when(trainerService.addPokemonToTrainer(any(Trainer.class), any(Pokemon.class)))
                .thenReturn(new PokemonDTO(pokemon));

        // Mockea el objeto Authentication con el trainer
        Authentication mockAuth = mock(Authentication.class);
        when(mockAuth.getPrincipal()).thenReturn(trainer);

        // Hace el POST al endpoint
        mockMvc.perform(post("/trainers/me/pokedex")
                        .principal(mockAuth)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(pokemon)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Pikachu")) // Comprueba que el nombre sea correcto
                .andExpect(jsonPath("$.attack").value(50)); // Comprueba el ataque

        // Verificamos que se llame al service solo una vez
        verify(trainerService, times(1)).addPokemonToTrainer(any(Trainer.class), any(Pokemon.class));
    }

    @Test
    @DisplayName("PATCH /trainers/me/coins -> suma monedas al entrenador")
    void shouldAddCoinsToTrainer() throws Exception {
        UpdateCoinsRequest request = new UpdateCoinsRequest();
        request.setAction("add");
        request.setCoins(50); // sumara 50 monedas al entrenador

        // Mock del service: devuelve un TrainerDTO actualizado con las monedas sumadas
        TrainerDTO updatedTrainerDTO = new TrainerDTO(trainer);
        updatedTrainerDTO.setCoins(trainer.getCoins() + 50); // valor esperado = 100 + 50
        when(trainerService.updateCoinsAndLevel(any(Trainer.class), any(UpdateCoinsRequest.class)))
                .thenReturn(updatedTrainerDTO);

        // Mockea el objeto Authentication con el trainer
        Authentication auth = mock(Authentication.class);
        when(auth.getPrincipal()).thenReturn(trainer);

        // Hace el PATCH al endpoint
        mockMvc.perform(patch("/trainers/me/coins")
                        .principal(auth) // Simula el usuario autenticado
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk()) // Espera 200 OK
                .andExpect(jsonPath("$.coins").value(150)); // Espera 150 monedas

        // Verifica que se haya llamado exactamente una vez al service
        verify(trainerService, times(1)).updateCoinsAndLevel(any(Trainer.class), any(UpdateCoinsRequest.class));;
    }

    @Test
    @DisplayName("DELETE /trainers/me/pokedex/{id} -> elimina Pokémon de la pokédex")
    void shouldRemovePokemonFromPokedex() throws Exception {
        Authentication auth = mock(Authentication.class);
        when(auth.getPrincipal()).thenReturn(trainer);

        // Mock del service: método void, solo se asegura que no lance excepción
        doNothing().when(trainerService).removePokemonFromTrainer(trainer, 1L);

        // Hace DELETE al endpoint
        mockMvc.perform(delete("/trainers/me/pokedex/{id}", 1L)
                        .principal(auth))
                .andExpect(status().isNoContent()); // Espera 204 No Content

        // Verificamos que se llame al service solo una vez
        verify(trainerService, times(1)).removePokemonFromTrainer(trainer, 1L);
    }
}
