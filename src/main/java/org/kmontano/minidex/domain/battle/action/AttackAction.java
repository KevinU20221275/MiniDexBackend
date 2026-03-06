package org.kmontano.minidex.domain.battle.action;

import org.kmontano.minidex.domain.battle.model.AttackResult;
import org.kmontano.minidex.domain.battle.model.BattleContext;
import org.kmontano.minidex.domain.battle.model.BattleSide;
import org.kmontano.minidex.domain.battle.service.AttackResolutionService;
import org.kmontano.minidex.domain.pokemon.Move;
import org.kmontano.minidex.domain.battle.event.AttackEventDTO;
import org.kmontano.minidex.domain.battle.event.BattleEventDTO;
import org.kmontano.minidex.dto.shared.BattlePokemon;

public class AttackAction implements BattleAction {
    private final Move move;
    private final BattleSide side;
    private final AttackResolutionService resolutionService;

    public AttackAction(Move move, BattleSide side, AttackResolutionService resolutionService) {
        this.move = move;
        this.side = side;
        this.resolutionService = resolutionService;
    }

    @Override
    public BattlePokemon getActor(BattleContext context) {
        return side == BattleSide.PLAYER ? context.getPlayer() : context.getEnemy();
    }

    @Override
    public BattleEventDTO execute(BattleContext context) {
        BattlePokemon attacker = getActor(context);
        BattlePokemon defender = side == BattleSide.PLAYER
                ? context.getEnemy() : context.getPlayer();

        int hpBefore = defender.getCurrentHp();

        AttackResult result = resolutionService.resolve(attacker, defender, move);

        if (result.isHit()){
            defender.setCurrentHp(Math.max(0, hpBefore - result.getDamage()));
        }

        return buildEvent(result, hpBefore, defender.getCurrentHp());
    }

    private AttackEventDTO buildEvent(AttackResult result, int hpBefore, int hpAfter){
        AttackEventDTO event = new AttackEventDTO();

        event.setSide(side);
        event.setMoveName(result.getMove().getMoveName());
        event.setMoveType(result.getMove().getType().toString());
        event.setHit(result.isHit());
        event.setDamage(result.getDamage());
        event.setHpBefore(hpBefore);
        event.setHpAfter(hpAfter);
        event.setEffectiveness(result.getEffectiveness());
        event.setHitResult(result.getHitResult());

        return event;
    }

    @Override
    public int getPriority() {
        return 0;
    }
}
