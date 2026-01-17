package org.kmontano.minidex.dto.response;

import org.kmontano.minidex.domain.pokemon.Rarity;
import org.kmontano.minidex.infrastructure.mapper.EvolutionChainResponse;
import org.kmontano.minidex.infrastructure.mapper.SpeciesResponse;

public class PokemonSpeciesData {

    private final SpeciesResponse speciesResponse;
    private final Rarity rarity;

    public PokemonSpeciesData(
            SpeciesResponse speciesResponse,
            Rarity rarity
    ) {
        this.speciesResponse = speciesResponse;
        this.rarity = rarity;
    }

    public SpeciesResponse getSpeciesResponse() {
        return speciesResponse;
    }

    public Rarity getRarity() { return rarity; }
}

