package org.kmontano.minidex.domain.battle.event;

import lombok.Data;
import org.kmontano.minidex.domain.battle.model.BattleEventTypes;

@Data
public class FinishBattleEventDTO extends BattleEventDTO {
    private Integer coins;
    private Integer previousXp;
    private Integer xpEarned;
    private Integer newXp;
    private boolean levelUp;
    private Integer level;
    private boolean playerWin;

    public FinishBattleEventDTO(){
        setType(BattleEventTypes.FINISH_BATTLE);
    }
}
