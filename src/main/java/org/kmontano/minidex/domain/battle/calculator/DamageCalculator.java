package org.kmontano.minidex.domain.battle.calculator;

import org.kmontano.minidex.domain.pokemon.Move;
import org.kmontano.minidex.dto.shared.BattlePokemon;

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
