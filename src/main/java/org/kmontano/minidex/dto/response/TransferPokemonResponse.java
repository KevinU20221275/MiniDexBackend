package org.kmontano.minidex.dto.response;

import lombok.Data;

@Data
public class TransferPokemonResponse {
    private int level;
    private int xp;
    private int coins;

    public TransferPokemonResponse(Integer level, Integer xp, Integer coins) {
        this.level = level;
        this.xp = xp;
        this.coins = coins;
    }
}
