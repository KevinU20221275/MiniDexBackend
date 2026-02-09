package org.kmontano.minidex.domain.battle.action;

import org.kmontano.minidex.domain.battle.BattleAction;
import org.kmontano.minidex.domain.battle.model.BattleContext;
import org.kmontano.minidex.domain.battle.model.BattleTurn;
import org.kmontano.minidex.dto.shared.BattlePokemon;

public class SwitchAction implements BattleAction {
    private final BattlePokemon nextPokemon;

    public SwitchAction(BattlePokemon nextPokemon) {
        this.nextPokemon = nextPokemon;
    }

    @Override
    public void execute(BattleContext context) {
        if (context.getCurrentTurn() == BattleTurn.ENEMY) {
            context.setEnemy(nextPokemon);
        } else {
            context.setPlayer(nextPokemon);
        }
    }
}
