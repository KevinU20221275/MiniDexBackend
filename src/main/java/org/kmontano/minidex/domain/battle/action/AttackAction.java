package org.kmontano.minidex.domain.battle.action;

import org.kmontano.minidex.domain.battle.*;
import org.kmontano.minidex.domain.battle.engine.TypeEffectivenessCalculator;
import org.kmontano.minidex.domain.battle.model.BattleContext;
import org.kmontano.minidex.domain.battle.model.BattleLogEntry;
import org.kmontano.minidex.domain.battle.model.BattleTurn;
import org.kmontano.minidex.domain.pokemon.Move;
import org.kmontano.minidex.dto.shared.BattlePokemon;

import java.util.concurrent.ThreadLocalRandom;

public class AttackAction implements BattleAction {
    private Move move;

    public AttackAction(Move randomMove) {
        this.move = randomMove;
    }

    @Override
    public void execute(BattleContext context) {
        BattlePokemon attacker = context.getCurrentTurn() == BattleTurn.PLAYER
                ? context.getPlayer()
                : context.getEnemy();

        BattlePokemon defender = attacker == context.getPlayer()
                ? context.getEnemy()
                : context.getPlayer();

        int accuracy = move.getAccuracy() != null ? move.getAccuracy() : 100;
        boolean hit = ThreadLocalRandom.current().nextInt(100) < accuracy;
        int hpBefore = defender.getCurrentHp();
        double effectiviness = 0.0;
        int baseDamage = 0;
        double stab = 0;
        int finalDamage = 0;

        if (hit) {
            effectiviness = TypeEffectivenessCalculator.calculate(move.getType(),defender.getTypes());

            int power = move.getPower() != null ? move.getPower() : 1;

            baseDamage = (attacker.getAttack() * power) / defender.getDefense();

            stab = attacker.getTypes().contains(move.getType()) ? 1.5 : 1.0;
            finalDamage = (int) Math.max(1, baseDamage * effectiviness * stab);

            defender.setCurrentHp(Math.max(0, defender.getCurrentHp() - finalDamage));
        }

        context.log(new BattleLogEntry(
                attacker.getName(),
                defender.getName(),
                move.getMoveName(),
                baseDamage,
                effectiviness,
                stab,
                finalDamage,
                hpBefore,
                defender.getCurrentHp()
        ));
    }
}
