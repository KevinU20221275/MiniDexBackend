package org.kmontano.minidex.infrastructure.mapper;

import lombok.Data;

@Data
public class SpeciesResponse {
    private EvolutionChain evolution_chain;
    private String name;
}
