package org.kmontano.minidex.factory;

import org.kmontano.minidex.domain.pokemon.PokemonType;
import org.kmontano.minidex.domain.pokemon.Move;
import org.kmontano.minidex.infrastructure.mapper.MoveResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Component
public class MoveFactory {
    private final RestTemplate restTemplate;

    public MoveFactory(RestTemplate restTemplate){
        this.restTemplate = restTemplate;
    }

    public Move fromMoveName(String moveName){
        String url = "https://pokeapi.co/api/v2/move/" + moveName;

        MoveResponse data = restTemplate.getForObject(url, MoveResponse.class);

        PokemonType type = PokemonType.fromApiName(data.getType().getName());

        return new Move(
                data.getName(),
                type,
                data.getPower(),
                data.getAccuracy()
        );
    }
}
