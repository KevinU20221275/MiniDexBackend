package org.kmontano.minidex.domain.battle.action;

import lombok.Data;
import org.kmontano.minidex.domain.battle.event.StrategicSwitchEventDTO;
import org.kmontano.minidex.domain.battle.model.BattleContext;
import org.kmontano.minidex.domain.battle.model.BattleSide;
import org.kmontano.minidex.domain.battle.event.BattleEventDTO;
import org.kmontano.minidex.domain.battle.event.SwitchEventDTO;
import org.kmontano.minidex.dto.shared.BattlePokemon;

/**
 * Battle action that changes the active Pokémon.
 *
 * <p>This action may represent two different types of switch:</p>
 *
 * - Forced switch (when a Pokémon faints)
 * - Strategic switch (voluntary trainer decision)
 *
 * Strategic switches consume the turn, while forced switches do not.
 */
@Data
public class SwitchAction implements BattleAction {
    private final BattlePokemon nextPokemon;
    private final BattleSide side;

    public SwitchAction(BattlePokemon nextPokemon, BattleSide side) {
        this.nextPokemon = nextPokemon;
        this.side = side;
    }

    @Override
    public BattlePokemon getActor(BattleContext context) {
        return side == BattleSide.PLAYER ? context.getPlayer() : context.getEnemy();
    }

    @Override
    public BattleEventDTO execute(BattleContext context) {
        boolean strategicSwitch;

        if (side == BattleSide.PLAYER){
            strategicSwitch = !context.getPlayer().isFainted();
            context.switchPlayer(nextPokemon);
        } else {
            strategicSwitch = !context.getEnemy().isFainted();
            context.switchEnemy(nextPokemon);
        }

        BattlePokemon snapshot = nextPokemon.copy();

        if (strategicSwitch){
            return new StrategicSwitchEventDTO(side, snapshot);
        }

        SwitchEventDTO event = new SwitchEventDTO();
        event.setSide(side);
        event.setNewPokemon(snapshot);

        return event;
    }

    @Override
    public int getPriority() {
        return 1;
    }
}
