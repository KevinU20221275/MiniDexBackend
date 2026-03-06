package org.kmontano.minidex.domain.battle.service;

import org.kmontano.minidex.domain.battle.calculator.DamageCalculator;
import org.kmontano.minidex.domain.battle.engine.TypeEffectivenessCalculator;
import org.kmontano.minidex.domain.battle.model.AttackResult;
import org.kmontano.minidex.domain.battle.model.HitResult;
import org.kmontano.minidex.domain.pokemon.Move;
import org.kmontano.minidex.dto.shared.BattlePokemon;
import org.springframework.stereotype.Component;

import java.util.concurrent.ThreadLocalRandom;

@Component
public class AttackResolutionService {
    private static final double CRIT_CHANCE = 0.0625;
    private static final double CRIT_MULTIPLIER = 1.5;
    private static final double LUCKY_MULTIPLIER = 1.1;

    public AttackResult resolve(BattlePokemon attacker, BattlePokemon defender, Move move){
        int accuracy = move.getAccuracy() != null ? move.getAccuracy() : 100;
        boolean hit = ThreadLocalRandom.current().nextInt(100) < accuracy;

        if (!hit) {
            return AttackResult.miss(move);
        }

        double effectiveness = TypeEffectivenessCalculator.calculate(move.getType(), defender.getTypes());

        double stab = attacker.getTypes().contains(move.getType()) ? 1.5 : 1.0;

        int baseDamage =
                DamageCalculator.calculate(
                        attacker,
                        defender,
                        move,
                        effectiveness,
                        stab
                );

        HitResult hitResult = HitResult.NORMAL;
        double modifier = 1.0;

        if (ThreadLocalRandom.current().nextDouble() < CRIT_CHANCE) {
            modifier *= CRIT_MULTIPLIER;
            hitResult = HitResult.CRITICAL;

            if (ThreadLocalRandom.current().nextDouble() < 0.25) {
                modifier *= LUCKY_MULTIPLIER;
                hitResult = HitResult.CRITICAL_LUCKY;
            }
        }

        int finalDamage = (int) Math.max(1, baseDamage * modifier);

        return AttackResult.success(move, finalDamage, effectiveness, hitResult);
    }
}
