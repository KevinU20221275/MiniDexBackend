package org.kmontano.minidex.dto.response;

import lombok.Data;

@Data
public class DailySpecialPokemonInfoDTO {
    private Integer price;
    private PackPokemon specialPokemon;

    public DailySpecialPokemonInfoDTO(Integer price, PackPokemon specialPokemon) {
        this.price = price;
        this.specialPokemon = specialPokemon;
    }
}
