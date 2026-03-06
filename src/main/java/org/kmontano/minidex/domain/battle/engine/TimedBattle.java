package org.kmontano.minidex.domain.battle.engine;

import lombok.Data;
import org.kmontano.minidex.domain.battle.model.BattleContext;

@Data
public class TimedBattle {
    private final BattleContext context;
    private final long createdAt;

    public boolean isExpired() {
        return System.currentTimeMillis() - createdAt > 30 * 60 * 1000;
    }
}
