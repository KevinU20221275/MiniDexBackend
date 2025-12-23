package org.kmontano.minidex.application.service;

import java.util.Optional;

public interface EvolutionService {
    Optional<String> getNextEvolutionName(String speciesUrl, String currentName);
    boolean canEvolve(String speciesUrl, String currentName);
}
