package org.kmontano.minidex.dto.response;

import lombok.Data;
import org.kmontano.minidex.dto.shared.BattlePokemon;

@Data
public class BattlePokemonStateResponse {
    private String pokemonId;
    private Integer maxHp;
    private Integer currentHp;
    private boolean fainted;

    public BattlePokemonStateResponse(BattlePokemon p) {
        this.pokemonId = p.getPokemonId();
        this.maxHp = p.getMaxHp();
        this.currentHp = p.getCurrentHp();
        this.fainted = p.isFainted();
    }
}
