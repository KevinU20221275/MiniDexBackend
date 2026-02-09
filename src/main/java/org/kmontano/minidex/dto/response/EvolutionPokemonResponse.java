package org.kmontano.minidex.dto.response;

import lombok.Data;
import org.kmontano.minidex.domain.pokemon.Pokemon;
import org.kmontano.minidex.domain.trainer.Trainer;

@Data
public class EvolutionPokemonResponse {
    private int coins;
    private int xp;
    private int level;
    private PokemonDTO evolvedPokemon;

    public EvolutionPokemonResponse(Trainer trainer, Pokemon evolvedPokemon) {
        this.coins = trainer.getCoins();
        this.xp = trainer.getXp();
        this.level = trainer.getLevel();
        this.evolvedPokemon = new PokemonDTO(evolvedPokemon);
    }
}
