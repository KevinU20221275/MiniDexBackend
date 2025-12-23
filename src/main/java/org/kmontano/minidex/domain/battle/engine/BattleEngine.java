package org.kmontano.minidex.domain.battle.engine;

import org.kmontano.minidex.domain.battle.BattleAction;
import org.kmontano.minidex.domain.battle.model.BattleContext;
import org.kmontano.minidex.domain.battle.model.BattleStatus;
import org.kmontano.minidex.domain.battle.model.BattleTurn;
import org.springframework.stereotype.Component;

@Component
public class BattleEngine {
    public void executeTurn(BattleContext context, BattleAction action){
        action.execute(context);

        if (context.getEnemy().isFainted()){
            context.setStatus(BattleStatus.PLAYER_WON);
            return;
        }

        if (context.getPlayer().isFainted()){
            context.setStatus(BattleStatus.ENEMY_WON);
            return;
        }

        context.setCurrentTurn(
                context.getCurrentTurn() == BattleTurn.PLAYER
                ? BattleTurn.ENEMY : BattleTurn.PLAYER
        );
    }
}
