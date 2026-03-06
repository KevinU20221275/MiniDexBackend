package org.kmontano.minidex.domain.battle.event;

import lombok.Data;
import org.kmontano.minidex.domain.battle.model.BattleEventTypes;
import org.kmontano.minidex.domain.battle.model.BattleSide;

@Data
public class FaintEventDTO extends BattleEventDTO {
    private BattleSide side;
    private String pokemonId;

    public FaintEventDTO() {
        setType(BattleEventTypes.FAINT);
    }

    public FaintEventDTO(BattleSide side, String pokemonId) {
        setType(BattleEventTypes.FAINT);
        this.side = side;
        this.pokemonId = pokemonId;
    }
}
