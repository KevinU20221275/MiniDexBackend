package org.kmontano.minidex.domain.battle.event;

import lombok.Data;
import org.kmontano.minidex.domain.battle.model.BattleEventTypes;
import org.kmontano.minidex.domain.battle.model.BattleSide;
import org.kmontano.minidex.domain.battle.model.HitResult;

@Data
public class AttackEventDTO extends BattleEventDTO {
    private boolean hit;
    private BattleSide side;
    private HitResult hitResult;
    private String moveName;
    private String moveType;
    private int damage;
    private int hpBefore;
    private int hpAfter;
    private double effectiveness;

    public AttackEventDTO() {
        setType(BattleEventTypes.ATTACK);
    }
}
