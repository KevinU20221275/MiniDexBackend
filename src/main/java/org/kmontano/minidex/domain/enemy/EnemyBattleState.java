package org.kmontano.minidex.domain.enemy;

import lombok.Data;

@Data
public class EnemyBattleState {
    private int turnsSinceLastSwitch = 99;
    private boolean forcedSwitch = false;

    public boolean canSwitch() {
        return forcedSwitch || turnsSinceLastSwitch >= 2;
    }

    public void forceSwitch() {
        this.forcedSwitch = true;
    }

    public void onSwitch() {
        this.turnsSinceLastSwitch = 0;
    }

    public void onTurnPassed() {
        this.turnsSinceLastSwitch++;
    }
}
