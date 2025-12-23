package org.kmontano.minidex.dto.response;

import org.kmontano.minidex.domain.battle.model.BattleContext;
import org.kmontano.minidex.domain.battle.model.BattleStatus;
import org.kmontano.minidex.domain.battle.model.BattleTurn;
import org.kmontano.minidex.dto.shared.BattlePokemon;

import java.util.List;

public class StartBattleResponse {
    private String battleId;
    private BattlePokemon currentPlayerPokemon;
    private BattlePokemon currentEnemyPokemon;
    private List<BattlePokemon> playerTeam;
    private List<BattlePokemon> enemyTeam;
    private BattleStatus status;
    private BattleTurn turn;
    private String enemyName;

    public StartBattleResponse(String battleId, BattleContext context, String enemyName) {
        this.battleId = battleId;
        this.currentPlayerPokemon = context.getPlayer();
        this.currentEnemyPokemon = context.getEnemy();
        this.playerTeam = context.getPlayerTeam();
        this.enemyTeam = context.getEnemyTeam();
        this.status = context.getStatus();
        this.turn = context.getCurrentTurn();
        this.enemyName = enemyName;
    }
}
