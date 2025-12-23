package org.kmontano.minidex.infrastructure.mapper;

import lombok.Data;

import java.util.List;

@Data
public class ChainLink {
    private List<ChainLink> evolves_to;
    private SpeciesResponse species;
}
