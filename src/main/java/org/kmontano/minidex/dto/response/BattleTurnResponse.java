package org.kmontano.minidex.dto.response;

import lombok.Data;
import org.kmontano.minidex.domain.battle.event.BattleEventDTO;
import org.kmontano.minidex.domain.battle.model.BattleContext;
import org.kmontano.minidex.domain.battle.model.BattleStatus;

import java.util.List;

@Data
public class BattleTurnResponse {
    private String battleId;
    private BattleStatus status;

    private BattlePokemonStateResponse player;
    private BattlePokemonStateResponse enemy;
    private List<BattleEventDTO> events;

    public BattleTurnResponse(BattleContext context, List<BattleEventDTO> events) {

        this.setBattleId(context.getBattleId());
        this.setStatus(context.getStatus());
        this.events = events;
        this.setPlayer(new BattlePokemonStateResponse(context.getPlayer()));
        this.setEnemy(new BattlePokemonStateResponse(context.getEnemy()));
    }
}
