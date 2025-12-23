package org.kmontano.minidex.domain.enemy;

import org.kmontano.minidex.domain.battle.engine.BattleStateEvaluator;
import org.kmontano.minidex.domain.battle.engine.TypeEffectivenessCalculator;
import org.kmontano.minidex.domain.enemy.decision.AiDecision;
import org.kmontano.minidex.domain.pokemon.Move;
import org.kmontano.minidex.dto.shared.BattlePokemon;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.Optional;

@Service
public class EnemyAiService {
    public AiDecision chooseMove(BattlePokemon enemy, BattlePokemon player){
        Optional<Move> bestMove = enemy.getMoves().stream()
                .filter(m -> m.getPower() != null)
                .max(Comparator.comparingDouble(
                        m -> TypeEffectivenessCalculator.calculate(m.getType(), player.getTypes())
                ));

        if (bestMove.isPresent()){
            double eff = TypeEffectivenessCalculator.calculate(bestMove.get().getType(), player.getTypes());

            if (eff > 1){
                return new AiDecision(bestMove.get(), "SUPER_EFFECTIVE");
            }
        }

        if (BattleStateEvaluator.isLowHp(enemy)){
            Move strongest = enemy.getMoves().stream()
                    .filter(m -> m.getPower() != null)
                    .max(Comparator.comparingInt(Move::getPower))
                    .orElse(enemy.getMoves().get(0));

            return new AiDecision(strongest, "DESPERATION_ATTACK");
        }

        Move safeMove = enemy.getMoves().stream()
                .filter(m -> m.getAccuracy() != null)
                .max(Comparator.comparingInt(Move::getAccuracy))
                .orElse(enemy.getMoves().get(0));

        return new AiDecision(safeMove, "SAFE_ATTACK");
    }
}
