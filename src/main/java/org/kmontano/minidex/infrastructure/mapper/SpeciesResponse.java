package org.kmontano.minidex.infrastructure.mapper;

import lombok.Data;

@Data
public class SpeciesResponse {
    private EvolutionChain evolution_chain;
    private String name;
    private boolean is_legendary;
    private boolean is_mythical;
    private Integer capture_rate;
}
