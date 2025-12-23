package org.kmontano.minidex.domain.battle;

import org.kmontano.minidex.domain.battle.model.BattleContext;
import org.springframework.stereotype.Component;

@Component
public interface BattleAction {
    void execute(BattleContext context);
}
