package org.kmontano.minidex.domain.enemy.decision;

import org.kmontano.minidex.domain.battle.engine.TypeEffectivenessCalculator;
import org.kmontano.minidex.dto.shared.BattlePokemon;
import org.springframework.stereotype.Component;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;

@Component
public class SwitchCandidateSelector {
    public Optional<BattlePokemon> findBestSwitch(
            BattlePokemon current,
            BattlePokemon player,
            List<BattlePokemon> team
    ) {
        return team.stream()
                .filter(p -> !p.isFainted())
                .filter(p -> !p.equals(current))
                .max(Comparator.comparingDouble(p ->
                        p.getTypes().stream()
                               .mapToDouble(type ->
                                       TypeEffectivenessCalculator.calculate(type, player.getTypes())
                               )
                                .max()
                                .orElse(1.0)
                ));
    }
}
