package org.kmontano.minidex.domain.enemy.decision;

import org.kmontano.minidex.domain.battle.engine.BattleStateEvaluator;
import org.kmontano.minidex.domain.battle.engine.TypeEffectivenessCalculator;
import org.kmontano.minidex.dto.shared.BattlePokemon;
import org.springframework.stereotype.Component;

@Component
public class SwitchDecisionPolicy {
    public boolean shouldSwitch(BattlePokemon active, BattlePokemon enemy) {

        boolean lowHp = BattleStateEvaluator.isLowHp(active);

        double effectiveness =
                TypeEffectivenessCalculator.calculate(
                        enemy.getTypes().get(0),
                        active.getTypes()
                );

        return lowHp || effectiveness > 1.5;
    }
}
