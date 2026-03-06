package org.kmontano.minidex.domain.battle.action;

import org.kmontano.minidex.domain.battle.model.BattleContext;
import org.kmontano.minidex.domain.battle.event.BattleEventDTO;
import org.kmontano.minidex.dto.shared.BattlePokemon;
import org.springframework.stereotype.Component;

@Component
public interface BattleAction {
    BattleEventDTO execute(BattleContext context);
    BattlePokemon getActor(BattleContext context);
    int getPriority();
}