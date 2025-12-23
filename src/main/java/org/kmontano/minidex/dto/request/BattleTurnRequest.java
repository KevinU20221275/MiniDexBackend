package org.kmontano.minidex.dto.request;

import lombok.Data;
import org.kmontano.minidex.dto.shared.ActionType;

@Data
public class BattleTurnRequest {
    private String battleId;
    private ActionType action;
    private String moveName;
    private String pokemonUuid;
}
