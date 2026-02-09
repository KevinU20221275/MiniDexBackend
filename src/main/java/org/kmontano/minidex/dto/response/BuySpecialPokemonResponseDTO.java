package org.kmontano.minidex.dto.response;

import lombok.Data;
import org.kmontano.minidex.domain.trainer.Trainer;

@Data
public class BuySpecialPokemonResponseDTO {
    private Integer coins;
    private Integer xp;
    private Integer level;

    public BuySpecialPokemonResponseDTO(Trainer trainer) {
        this.coins = trainer.getCoins();
        this.xp = trainer.getXp();
        this.level = trainer.getLevel();
    }
}
