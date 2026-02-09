package org.kmontano.minidex.dto.response;

import lombok.Data;
import org.kmontano.minidex.domain.battle.model.BattleContext;
import org.kmontano.minidex.domain.battle.model.BattleStatus;
import org.kmontano.minidex.domain.battle.model.BattleTurn;
import org.kmontano.minidex.dto.shared.BattlePokemon;

@Data
public class BattleTurnResponse {
    private BattlePokemon player;
    private BattlePokemon enemy;
    private BattleStatus status;
    private BattleTurn turn;

    public BattleTurnResponse(BattleContext context) {
        this.player = context.getPlayer();
        this.enemy = context.getEnemy();
        this.status = context.getStatus();
        this.turn = context.getCurrentTurn();
    }
}
