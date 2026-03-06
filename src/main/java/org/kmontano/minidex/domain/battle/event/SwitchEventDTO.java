package org.kmontano.minidex.domain.battle.event;

import lombok.Data;
import org.kmontano.minidex.domain.battle.model.BattleEventTypes;
import org.kmontano.minidex.domain.battle.model.BattleSide;
import org.kmontano.minidex.dto.shared.BattlePokemon;

@Data
public class SwitchEventDTO extends BattleEventDTO {
    private BattleSide side;
    private BattlePokemon newPokemon;

    public SwitchEventDTO() {
        setType(BattleEventTypes.SWITCH);
    }

    public SwitchEventDTO(BattleSide side, BattlePokemon newPokemon) {
        setType(BattleEventTypes.SWITCH);
        this.newPokemon = newPokemon;
        this.side = side;
    }
}
