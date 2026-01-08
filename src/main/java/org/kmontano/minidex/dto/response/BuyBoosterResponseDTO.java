package org.kmontano.minidex.dto.response;

import java.util.List;

public class BuyBoosterResponseDTO {
    List<PackPokemon> pokemons;

    public BuyBoosterResponseDTO(List<PackPokemon> pokemons) {
        this.pokemons = pokemons;
    }
}
