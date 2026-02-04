package org.kmontano.minidex.application.service;

import org.kmontano.minidex.domain.pokemon.NextEvolution;
import org.kmontano.minidex.dto.response.PokemonSpeciesData;
import org.kmontano.minidex.infrastructure.mapper.SpeciesResponse;

import java.util.Optional;

public interface EvolutionService {
    Optional<NextEvolution> getNextEvolution(SpeciesResponse species, String currentName);
    PokemonSpeciesData getSpeciesData(String speciesUrl);
}
