package org.kmontano.minidex.dto.response;

import lombok.Data;
import org.kmontano.minidex.domain.pokemon.Pokemon;
import org.kmontano.minidex.domain.trainer.Trainer;

@Data
public class EvolutionPokemonResponse {
    private int trainerCoins;
    private int trainerXp;
    private int trainerLevel;
    private PokemonDTO evolvedPokemon;

    public EvolutionPokemonResponse(Trainer trainer, Pokemon evolvedPokemon) {
        this.trainerCoins = trainer.getCoins();
        this.trainerXp = trainer.getXp();
        this.trainerLevel = trainer.getLevel();
        this.evolvedPokemon = new PokemonDTO(evolvedPokemon);
    }
}
