package org.kmontano.minidex.domain.enemy.service;

import org.kmontano.minidex.domain.battle.action.AttackAction;
import org.kmontano.minidex.domain.battle.action.BattleAction;
import org.kmontano.minidex.domain.battle.service.AttackResolutionService;
import org.kmontano.minidex.domain.battle.model.BattleSide;
import org.kmontano.minidex.domain.battle.action.SwitchAction;
import org.kmontano.minidex.domain.battle.model.BattleContext;
import org.kmontano.minidex.domain.enemy.EnemyAiService;
import org.kmontano.minidex.domain.enemy.EnemyBattleState;
import org.kmontano.minidex.domain.enemy.decision.AiDecision;
import org.kmontano.minidex.domain.enemy.decision.SwitchCandidateSelector;
import org.kmontano.minidex.domain.enemy.decision.SwitchDecisionPolicy;
import org.kmontano.minidex.dto.shared.BattlePokemon;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * Service responsible for deciding enemy actions during battle.
 *
 * <p>The AI evaluates the current battle state and selects the
 * most appropriate action.</p>
 *
 * Possible decisions:
 * - Attack using one of the available moves
 * - Perform a strategic switch
 *
 * The decision may consider:
 * - Pokémon HP
 * - Type matchups
 * - Move availability
 */
@Service
public class EnemyAiDecisionService {
    private final SwitchDecisionPolicy switchPolicy;
    private final SwitchCandidateSelector selector;
    private final EnemyAiService enemyAiService;
    private final AttackResolutionService attackResolutionService;

    private final Map<String, EnemyBattleState> states = new HashMap<>();

    public EnemyAiDecisionService(
            SwitchDecisionPolicy switchPolicy,
            SwitchCandidateSelector selector,
            EnemyAiService enemyAiService,
            AttackResolutionService attackResolutionService
    ) {
        this.switchPolicy = switchPolicy;
        this.selector = selector;
        this.enemyAiService = enemyAiService;
        this.attackResolutionService = attackResolutionService;
    }

    public BattleAction decide(BattleContext context){
        BattlePokemon enemy = context.getEnemy();
        BattlePokemon player = context.getPlayer();

        EnemyBattleState state = states.computeIfAbsent(
                context.getBattleId(),
                id -> new EnemyBattleState()
        );

        state.onTurnPassed();

        if (enemy.isFainted()){
            state.forceSwitch();
            return forceSwitch(context);
        }

        if (state.canSwitch() && switchPolicy.shouldSwitch(enemy, player)){
            Optional<BattlePokemon> candidate = selector.findBestSwitch(enemy, player, context.getEnemyTeam());

            if (candidate.isPresent()){
                state.onSwitch();
                return new SwitchAction(candidate.get(), BattleSide.ENEMY);
            }
        }

        AiDecision decision = enemyAiService.chooseMove(enemy, player);

        return new AttackAction(decision.getSelectedMove(), BattleSide.ENEMY, attackResolutionService);
    }

    private BattleAction forceSwitch(BattleContext context) {
        Optional<BattlePokemon> candidate =
                selector.findBestSwitch(
                        context.getEnemy(),
                        context.getPlayer(),
                        context.getEnemyTeam()
                );

        return candidate
                .<BattleAction>map(p -> new SwitchAction(p, BattleSide.ENEMY))
                .orElseThrow(() ->
                        new IllegalStateException("Enemy has no pokemon left")
                );
    }

    public Optional<BattlePokemon> switchByKO(BattlePokemon current, BattlePokemon player, List<BattlePokemon> team){
        return selector.findBestSwitch(current, player, team);
    }
}
