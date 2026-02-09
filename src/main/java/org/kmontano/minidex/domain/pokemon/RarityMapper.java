package org.kmontano.minidex.domain.pokemon;

import org.kmontano.minidex.infrastructure.mapper.SpeciesResponse;

public class RarityMapper {

    public static Rarity fromSpecies(SpeciesResponse species) {

        if (species.is_legendary()) {
            return Rarity.LEGENDARY;
        }

        if (species.is_mythical()) {
            return Rarity.EPIC;
        }

        int rate = species.getCapture_rate();

        if (rate <= 30) return Rarity.RARE;
        if (rate <= 90) return Rarity.UNCOMMON;

        return Rarity.COMMON;
    }
}

