package org.kmontano.minidex.dto.response;

import lombok.Data;

@Data
public class PokemonStoreDTO {

    private PackPokemon specialPokemon;
    private int specialPokemonPrice;
    private boolean specialPokemonPurchased;

    private int boosterPrice;
    private int boostersRemaining;

    public PokemonStoreDTO(PackPokemon specialPokemon, int specialPokemonPrice, boolean specialPokemonPurchased, int boosterPrice, int boostersRemaining) {
        this.specialPokemon = specialPokemon;
        this.specialPokemonPrice = specialPokemonPrice;
        this.specialPokemonPurchased = specialPokemonPurchased;
        this.boosterPrice = boosterPrice;
        this.boostersRemaining = boostersRemaining;
    }
}
