package org.kmontano.minidex.dto.response;

import lombok.Data;
import org.kmontano.minidex.domain.pokemon.Pokemon;

@Data
public class EvolutionPokemonResponse {
    private int trainerCoins;
    private int trainerXp;
    private int trainerLevel;
    private PokemonDTO evolvedPokemon;

    public EvolutionPokemonResponse(int trainerCoins, int trainerXp, int trainerLevel, Pokemon evolvedPokemon) {
        this.trainerCoins = trainerCoins;
        this.trainerXp = trainerXp;
        this.trainerLevel = trainerLevel;
        this.evolvedPokemon = new PokemonDTO(evolvedPokemon);
    }
}
