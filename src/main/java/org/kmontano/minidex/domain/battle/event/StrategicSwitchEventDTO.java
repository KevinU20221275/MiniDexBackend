package org.kmontano.minidex.domain.battle.event;

import lombok.Data;
import org.kmontano.minidex.domain.battle.model.BattleEventTypes;
import org.kmontano.minidex.domain.battle.model.BattleSide;
import org.kmontano.minidex.dto.shared.BattlePokemon;

@Data
public class StrategicSwitchEventDTO extends BattleEventDTO {
    private BattleSide side;
    private BattlePokemon newPokemon;

    public StrategicSwitchEventDTO() {
        setType(BattleEventTypes.STRATEGIC_SWITCH);
    }

    public StrategicSwitchEventDTO(BattleSide side, BattlePokemon newPokemon) {
        setType(BattleEventTypes.STRATEGIC_SWITCH);
        this.side = side;
        this.newPokemon = newPokemon;
    }
}
