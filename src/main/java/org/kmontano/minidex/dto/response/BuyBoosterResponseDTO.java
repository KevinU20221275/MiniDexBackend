package org.kmontano.minidex.dto.response;

import lombok.Data;
import org.kmontano.minidex.domain.trainer.Trainer;

import java.util.List;

@Data
public class BuyBoosterResponseDTO {
    private Integer coins;
    private Integer xp;
    private Integer level;
    private List<PackPokemon> pokemons;

    public BuyBoosterResponseDTO(Trainer trainer, List<PackPokemon> pokemons) {
        this.coins = trainer.getCoins();
        this.xp = trainer.getXp();
        this.level = trainer.getLevel();
        this.pokemons = pokemons;
    }
}
