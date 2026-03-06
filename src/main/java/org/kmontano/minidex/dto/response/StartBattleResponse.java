package org.kmontano.minidex.dto.response;

import lombok.Data;
import org.kmontano.minidex.domain.battle.model.BattleContext;
import org.kmontano.minidex.domain.battle.model.BattleStatus;
import org.kmontano.minidex.dto.shared.BattlePokemon;

import java.util.List;

@Data
public class StartBattleResponse {
    private String battleId;
    private BattlePokemon currentPlayerPokemon;
    private BattlePokemon currentEnemyPokemon;
    private List<BattlePokemon> playerTeam;
    private BattleStatus status;
    private String enemyName;

    public StartBattleResponse(String battleId, BattleContext context) {
        this.battleId = battleId;
        this.currentPlayerPokemon = context.getPlayer();
        this.currentEnemyPokemon = context.getEnemy();
        this.playerTeam = context.getPlayerTeam();;
        this.status = context.getStatus();
        this.enemyName = "Team Rocket";
    }
}
