package org.kmontano.minidex.domain.enemy.service;

import org.kmontano.minidex.domain.battle.action.AttackAction;
import org.kmontano.minidex.domain.battle.BattleAction;
import org.kmontano.minidex.domain.battle.action.SwitchAction;
import org.kmontano.minidex.domain.enemy.EnemyAiService;
import org.kmontano.minidex.domain.enemy.EnemyBattleState;
import org.kmontano.minidex.domain.enemy.decision.AiDecision;
import org.kmontano.minidex.domain.enemy.decision.SwitchCandidateSelector;
import org.kmontano.minidex.domain.enemy.decision.SwitchDecisionPolicy;
import org.kmontano.minidex.dto.shared.BattlePokemon;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class EnemyAiDecisionService {
    private final SwitchDecisionPolicy switchPolicy;
    private final SwitchCandidateSelector selector;
    private EnemyAiService enemyAiService;

    public EnemyAiDecisionService(
            SwitchDecisionPolicy switchPolicy,
            SwitchCandidateSelector selector,
            EnemyAiService enemyAiService
    ) {
        this.switchPolicy = switchPolicy;
        this.selector = selector;
        this.enemyAiService = enemyAiService;
    }

    public BattleAction decide(
            BattlePokemon enemy,
            BattlePokemon player,
            List<BattlePokemon> team,
            EnemyBattleState state
    ) {

        if (state.canSwitch()
                && switchPolicy.shouldSwitch(enemy, player)) {

            return selector.findBestSwitch(
                    enemy, player, team
            ).<BattleAction>map(pokemon -> {
                state.onSwitch();
                return new SwitchAction(pokemon);
            }).orElseGet(() -> {
                state.onTurnPassed();
                return attack(enemy, player);
            });
        }

        state.onTurnPassed();
        return attack(enemy, player);
    }

    public Optional<BattlePokemon> swicthByKO(BattlePokemon current, BattlePokemon player, List<BattlePokemon> team){
        return selector.findBestSwitch(current, player, team);
    }

    private BattleAction attack(
            BattlePokemon enemy,
            BattlePokemon player
    ) {
        AiDecision decision =
                enemyAiService.chooseMove(enemy, player);

        return new AttackAction(decision.getSelectedMove());
    }

}
