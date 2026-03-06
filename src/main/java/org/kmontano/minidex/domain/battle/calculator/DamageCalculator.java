package org.kmontano.minidex.domain.battle.calculator;

import org.kmontano.minidex.domain.pokemon.Move;
import org.kmontano.minidex.dto.shared.BattlePokemon;

/**
 * Utility responsible for calculating base damage of an attack.
 *
 * <p>This class implements the simplified damage formula used
 * by the battle engine.</p>
 *
 * Factors used:
 * - Attacker level
 * - Move power
 * - Attack stat
 * - Defense stat
 * - Type effectiveness
 * - STAB (Same Type Attack Bonus)
 *
 * The final result is always at least 1 damage.
 */
public class DamageCalculator {
    public static int calculate(BattlePokemon attacker, BattlePokemon defender, Move move, double effectiveness, double stab){
        int balanceMultiplier = 4;
        int level = attacker.getLevel();
        int power = move.getPower() != null ? move.getPower() : 20;

        double base = ((double) ((2 * level / 5 + 2) * power * attacker.getAttack()) / defender.getDefense());

        int damage = (int) (((base / 50.0) + 2) * effectiveness * stab) * balanceMultiplier;

        return Math.max(1, damage);
    }
}
