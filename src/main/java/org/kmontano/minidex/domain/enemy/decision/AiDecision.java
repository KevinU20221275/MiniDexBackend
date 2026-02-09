package org.kmontano.minidex.domain.enemy.decision;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.kmontano.minidex.domain.pokemon.Move;

@Data
@AllArgsConstructor
public class AiDecision {
    private Move selectedMove;
    private String reason;
}
