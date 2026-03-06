package org.kmontano.minidex.domain.battle.model;

import lombok.Data;
import org.kmontano.minidex.domain.pokemon.Move;

@Data
public class AttackResult {
    private final Move move;
    private final boolean hit;
    private final int damage;
    private final double effectiveness;
    private final HitResult hitResult;

    public AttackResult(Move move, boolean hit, int damage, double effectiveness, HitResult hitResult) {
        this.move = move;
        this.hit = hit;
        this.damage = damage;
        this.effectiveness = effectiveness;
        this.hitResult = hitResult;
    }

    public static AttackResult miss(Move move) {
        return new AttackResult(move, false, 0, 1.0, HitResult.MISS);
    }

    public static AttackResult status(Move move) {
        return new AttackResult(move, true, 0, 1.0, HitResult.NO_EFFECT);
    }

    public static AttackResult success(
            Move move,
            int damage,
            double effectiveness,
            HitResult hitResult
    ) {
        return new AttackResult(move, true, damage, effectiveness, hitResult);
    }
}
