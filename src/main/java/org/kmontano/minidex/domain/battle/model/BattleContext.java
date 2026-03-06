package org.kmontano.minidex.domain.battle.model;

import lombok.Data;
import org.kmontano.minidex.dto.shared.BattlePokemon;

import java.util.List;

// pepe empezamos
@Data
public class BattleContext {
    private String battleId;
    private String trainerId;
    private BattlePokemon player;
    private BattlePokemon enemy;
    private List<BattlePokemon> playerTeam;
    private List<BattlePokemon> enemyTeam;
    private BattleStatus status;

    public BattleContext(BattlePokemon player, BattlePokemon enemy, List<BattlePokemon> playerTeam, List<BattlePokemon> enemyTeam, String trainerId) {
        this.trainerId = trainerId;
        this.player = player;
        this.enemy = enemy;
        this.status = BattleStatus.IN_PROGRESS;
        this.playerTeam = playerTeam;
        this.enemyTeam = enemyTeam;
    }

    public void switchPlayer(BattlePokemon next) {
        this.player = next;
    }

    public void switchEnemy(BattlePokemon next) {
        this.enemy = next;
    }

    public boolean isPlayerDefeated() {
        return playerTeam.stream().allMatch(BattlePokemon::isFainted);
    }

    public boolean isEnemyDefeated() {
        return enemyTeam.stream().allMatch(BattlePokemon::isFainted);
    }

    public void endBattle(BattleStatus result) {
        this.status = result;
    }
}
