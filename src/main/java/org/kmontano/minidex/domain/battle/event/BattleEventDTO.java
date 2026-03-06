package org.kmontano.minidex.domain.battle.event;

import lombok.Data;
import org.kmontano.minidex.domain.battle.model.BattleEventTypes;

@Data
public abstract class BattleEventDTO {
    private BattleEventTypes type;
}
