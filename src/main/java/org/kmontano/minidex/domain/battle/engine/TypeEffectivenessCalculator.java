package org.kmontano.minidex.domain.battle.engine;

import org.kmontano.minidex.domain.pokemon.PokemonType;

import java.util.List;
import java.util.Map;

public class TypeEffectivenessCalculator {
    private static final double EFFECTIVE_MULTIPLIER = 2.0;
    private static final double NOT_EFFECTIVE = 0.5;

    private static final Map<PokemonType, Map<PokemonType, Double>> EFFECTIVENESS = Map.ofEntries(
            Map.entry(
                    PokemonType.BUG, Map.of(
                            PokemonType.FIRE, NOT_EFFECTIVE,
                            PokemonType.FLYING, NOT_EFFECTIVE,
                            PokemonType.ROCK, NOT_EFFECTIVE,
                            PokemonType.DARK, EFFECTIVE_MULTIPLIER,
                            PokemonType.GRASS, EFFECTIVE_MULTIPLIER,
                            PokemonType.PSYCHIC, EFFECTIVE_MULTIPLIER
                    )
            ),

            Map.entry(
                    PokemonType.DARK, Map.of(
                            PokemonType.BUG, NOT_EFFECTIVE,
                            PokemonType.FAIRY, NOT_EFFECTIVE,
                            PokemonType.FIGHTING, NOT_EFFECTIVE,
                            PokemonType.GHOST, EFFECTIVE_MULTIPLIER,
                            PokemonType.PSYCHIC, EFFECTIVE_MULTIPLIER
                    )
            ),

            Map.entry(
                    PokemonType.DRAGON, Map.of(
                            PokemonType.FAIRY, NOT_EFFECTIVE,
                            PokemonType.ICE, NOT_EFFECTIVE,
                            PokemonType.DRAGON, EFFECTIVE_MULTIPLIER
                    )
            ),

            Map.entry(
                    PokemonType.ELECTRIC, Map.of(
                            PokemonType.WATER, EFFECTIVE_MULTIPLIER,
                            PokemonType.FLYING, EFFECTIVE_MULTIPLIER,
                            PokemonType.GROUND, NOT_EFFECTIVE
                    )
            ),

            Map.entry(
                    PokemonType.FAIRY, Map.of(
                            PokemonType.POISON, NOT_EFFECTIVE,
                            PokemonType.STEEL, NOT_EFFECTIVE,
                            PokemonType.DARK, EFFECTIVE_MULTIPLIER,
                            PokemonType.DRAGON, EFFECTIVE_MULTIPLIER,
                            PokemonType.FIGHTING, EFFECTIVE_MULTIPLIER
                    )
            ),

            Map.entry(
                    PokemonType.FIGHTING, Map.of(
                            PokemonType.FAIRY, NOT_EFFECTIVE,
                            PokemonType.FLYING, NOT_EFFECTIVE,
                            PokemonType.PSYCHIC, NOT_EFFECTIVE,
                            PokemonType.DARK, EFFECTIVE_MULTIPLIER,
                            PokemonType.ICE, EFFECTIVE_MULTIPLIER,
                            PokemonType.NORMAL, EFFECTIVE_MULTIPLIER,
                            PokemonType.ROCK, EFFECTIVE_MULTIPLIER,
                            PokemonType.STEEL, EFFECTIVE_MULTIPLIER
                    )
            ),

            Map.entry(
                    PokemonType.FIRE, Map.of(
                            PokemonType.GROUND, NOT_EFFECTIVE,
                            PokemonType.ROCK, NOT_EFFECTIVE,
                            PokemonType.WATER, NOT_EFFECTIVE,
                            PokemonType.BUG, EFFECTIVE_MULTIPLIER,
                            PokemonType.GRASS, EFFECTIVE_MULTIPLIER,
                            PokemonType.ICE, EFFECTIVE_MULTIPLIER,
                            PokemonType.STEEL, EFFECTIVE_MULTIPLIER
                    )
            ),

            Map.entry(
                    PokemonType.FLYING, Map.of(
                            PokemonType.ELECTRIC, NOT_EFFECTIVE,
                            PokemonType.ICE, NOT_EFFECTIVE,
                            PokemonType.ROCK, NOT_EFFECTIVE,
                            PokemonType.BUG, EFFECTIVE_MULTIPLIER,
                            PokemonType.GRASS, EFFECTIVE_MULTIPLIER,
                            PokemonType.FIGHTING, EFFECTIVE_MULTIPLIER
                    )
            ),

            Map.entry(
                    PokemonType.GHOST, Map.of(
                            PokemonType.DARK, NOT_EFFECTIVE,
                            PokemonType.PSYCHIC, EFFECTIVE_MULTIPLIER,
                            PokemonType.GHOST, EFFECTIVE_MULTIPLIER
                    )
            ),

            Map.entry(
                    PokemonType.GRASS, Map.of(
                            PokemonType.BUG, NOT_EFFECTIVE,
                            PokemonType.FIRE, NOT_EFFECTIVE,
                            PokemonType.FIGHTING, NOT_EFFECTIVE,
                            PokemonType.ICE, NOT_EFFECTIVE,
                            PokemonType.POISON, NOT_EFFECTIVE,
                            PokemonType.GROUND, EFFECTIVE_MULTIPLIER,
                            PokemonType.ROCK, EFFECTIVE_MULTIPLIER,
                            PokemonType.WATER, EFFECTIVE_MULTIPLIER
                    )
            ),

            Map.entry(
                    PokemonType.GROUND, Map.of(
                            PokemonType.GRASS, NOT_EFFECTIVE,
                            PokemonType.ICE, NOT_EFFECTIVE,
                            PokemonType.WATER, NOT_EFFECTIVE,
                            PokemonType.ELECTRIC, EFFECTIVE_MULTIPLIER,
                            PokemonType.FIRE, EFFECTIVE_MULTIPLIER,
                            PokemonType.POISON, EFFECTIVE_MULTIPLIER,
                            PokemonType.ROCK, EFFECTIVE_MULTIPLIER,
                            PokemonType.STEEL, EFFECTIVE_MULTIPLIER
                    )
            ),

            Map.entry(
                    PokemonType.ICE, Map.of(
                            PokemonType.FIGHTING, NOT_EFFECTIVE,
                            PokemonType.FIRE, NOT_EFFECTIVE,
                            PokemonType.ROCK, NOT_EFFECTIVE,
                            PokemonType.STEEL, NOT_EFFECTIVE,
                            PokemonType.DRAGON, EFFECTIVE_MULTIPLIER,
                            PokemonType.FLYING, EFFECTIVE_MULTIPLIER,
                            PokemonType.GRASS, EFFECTIVE_MULTIPLIER,
                            PokemonType.GROUND, EFFECTIVE_MULTIPLIER
                    )
            ),

            Map.entry(
                    PokemonType.NORMAL, Map.of(
                            PokemonType.FIGHTING, NOT_EFFECTIVE
                    )
            ),

            Map.entry(
                    PokemonType.POISON, Map.of(
                            PokemonType.GROUND, NOT_EFFECTIVE,
                            PokemonType.PSYCHIC, NOT_EFFECTIVE,
                            PokemonType.FAIRY, EFFECTIVE_MULTIPLIER,
                            PokemonType.GRASS, EFFECTIVE_MULTIPLIER
                    )
            ),

            Map.entry(
                    PokemonType.PSYCHIC, Map.of(
                            PokemonType.BUG, NOT_EFFECTIVE,
                            PokemonType.DARK, NOT_EFFECTIVE,
                            PokemonType.GHOST, NOT_EFFECTIVE,
                            PokemonType.FIGHTING, EFFECTIVE_MULTIPLIER,
                            PokemonType.POISON, EFFECTIVE_MULTIPLIER
                    )
            ),

            Map.entry(
                    PokemonType.ROCK, Map.of(
                            PokemonType.FIGHTING, NOT_EFFECTIVE,
                            PokemonType.GRASS, NOT_EFFECTIVE,
                            PokemonType.GROUND, NOT_EFFECTIVE,
                            PokemonType.STEEL, NOT_EFFECTIVE,
                            PokemonType.WATER, NOT_EFFECTIVE,
                            PokemonType.BUG, EFFECTIVE_MULTIPLIER,
                            PokemonType.FIRE, EFFECTIVE_MULTIPLIER,
                            PokemonType.FLYING, EFFECTIVE_MULTIPLIER,
                            PokemonType.ICE, EFFECTIVE_MULTIPLIER
                    )
            ),

            Map.entry(
                    PokemonType.STEEL, Map.of(
                            PokemonType.FIGHTING, NOT_EFFECTIVE,
                            PokemonType.FIRE, NOT_EFFECTIVE,
                            PokemonType.GROUND, NOT_EFFECTIVE,
                            PokemonType.FAIRY, EFFECTIVE_MULTIPLIER,
                            PokemonType.ICE, EFFECTIVE_MULTIPLIER,
                            PokemonType.ROCK, EFFECTIVE_MULTIPLIER
                    )
            ),

            Map.entry(
                    PokemonType.WATER, Map.of(
                            PokemonType.ELECTRIC, NOT_EFFECTIVE,
                            PokemonType.GRASS, NOT_EFFECTIVE,
                            PokemonType.FIRE, EFFECTIVE_MULTIPLIER,
                            PokemonType.GROUND, EFFECTIVE_MULTIPLIER,
                            PokemonType.ROCK, EFFECTIVE_MULTIPLIER
                    )
            )
    );

    public static double calculate(PokemonType moveType, List<PokemonType> defenderTypes) {
        double multiplier = 1.0;

        Map<PokemonType, Double> rules = EFFECTIVENESS.getOrDefault(moveType, Map.of());

        for (PokemonType defenderType : defenderTypes){
            multiplier *= rules.getOrDefault(defenderType, 1.0);
        }

        return multiplier;
    }
}
