package org.kmontano.minidex.dto.response;

import lombok.Data;
import org.kmontano.minidex.domain.pokemon.Pokemon;

@Data
public class EvolPokemonResponse {
    private int trainerCoins;
    private int trainerXp;
    private int trainerLevel;
    private Pokemon evolvedPokemon;

    public EvolPokemonResponse(int trainerCoins, int trainerXp, int trainerLevel, Pokemon evolvedPokemon) {
        this.trainerCoins = trainerCoins;
        this.trainerXp = trainerXp;
        this.trainerLevel = trainerLevel;
        this.evolvedPokemon = evolvedPokemon;
    }
}
