package org.kmontano.minidex.dto.response;

import lombok.Data;

import java.util.List;

@Data
public class BuyBoosterResponseDTO {
    List<PackPokemon> pokemons;

    public BuyBoosterResponseDTO(List<PackPokemon> pokes) {
        this.pokemons = pokes;
    }
}
