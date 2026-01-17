package org.kmontano.minidex.dto.response;

import lombok.Data;

import java.util.List;

@Data
public class BoosterResponseDTO {
    private List<PackPokemon> pokemons;

    public BoosterResponseDTO(List<PackPokemon> pokes) {
        this.pokemons = pokes;
    }
}
