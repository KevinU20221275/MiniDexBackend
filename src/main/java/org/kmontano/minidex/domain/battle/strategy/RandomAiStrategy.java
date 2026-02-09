package org.kmontano.minidex.domain.battle.strategy;

import org.kmontano.minidex.domain.battle.BattleAction;
import org.kmontano.minidex.domain.battle.action.AttackAction;
import org.kmontano.minidex.domain.battle.model.BattleContext;
import org.kmontano.minidex.domain.pokemon.Move;

public class RandomAiStrategy {
    public BattleAction chooseAction(BattleContext context) {
        Move randomMove = context.getEnemy().getMoves().get(0);
        return new AttackAction(randomMove);
    }
}
