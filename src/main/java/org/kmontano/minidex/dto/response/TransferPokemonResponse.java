package org.kmontano.minidex.dto.response;

import lombok.Data;
import org.kmontano.minidex.domain.trainer.Trainer;

@Data
public class TransferPokemonResponse {
    private int level;
    private int xp;
    private int coins;

    public TransferPokemonResponse(Trainer trainer) {
        this.level = trainer.getLevel();
        this.xp = trainer.getXp();
        this.coins = trainer.getCoins();
    }
}
