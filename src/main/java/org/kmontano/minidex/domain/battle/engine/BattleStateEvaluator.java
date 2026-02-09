package org.kmontano.minidex.domain.battle.engine;

import org.kmontano.minidex.dto.shared.BattlePokemon;

public class BattleStateEvaluator {
    public static boolean isLowHp(BattlePokemon pokemon){
        return pokemon.getCurrentHp() <= pokemon.getMaxHp() * 0.3;
    }

    public static double hpPercent(BattlePokemon pokemon){
        return (double) pokemon.getCurrentHp() / pokemon.getMaxHp();
    }
}
