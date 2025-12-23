package org.kmontano.minidex.infrastructure.api;

import org.kmontano.minidex.infrastructure.mapper.PokemonResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Component
public class PokemonApiClient {

    private static final String BASE_URL = "https://pokeapi.co/api/v2/pokemon/";

    private final RestTemplate restTemplate;

    public PokemonApiClient(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public PokemonResponse getPokemonById(int id) {
        return restTemplate.getForObject(
                BASE_URL + id,
                PokemonResponse.class
        );
    }

    public PokemonResponse getPokemonByName(String name) {
        return restTemplate.getForObject(
                BASE_URL + name.toLowerCase(),
                PokemonResponse.class
        );
    }
}
