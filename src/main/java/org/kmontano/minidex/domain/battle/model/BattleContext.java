package org.kmontano.minidex.domain.battle.model;

import lombok.Data;
import org.kmontano.minidex.domain.enemy.EnemyBattleState;
import org.kmontano.minidex.dto.shared.BattlePokemon;

import java.util.ArrayList;
import java.util.List;

@Data
public class BattleContext {
    private BattlePokemon player;
    private BattlePokemon enemy;
    private BattleTurn currentTurn;
    private BattleStatus status;
    private EnemyBattleState enemyBattleState;
    private List<BattlePokemon> playerTeam;
    private List<BattlePokemon> enemyTeam;
    private List<BattleLogEntry> logs = new ArrayList<>();

    public BattleContext(BattlePokemon player, BattlePokemon enemy, BattleTurn currentTurn, BattleStatus status) {
        this.player = player;
        this.enemy = enemy;
        this.currentTurn = currentTurn;
        this.status = status;
        this.enemyBattleState = new EnemyBattleState();
    }

    public void log(BattleLogEntry entry) {
        this.logs.add(entry);
    }

}
