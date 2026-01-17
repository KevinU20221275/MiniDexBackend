package org.kmontano.minidex.factory;

import org.kmontano.minidex.application.service.EvolutionService;
import org.kmontano.minidex.dto.response.PackPokemon;
import org.kmontano.minidex.dto.response.PokemonSpeciesData;
import org.kmontano.minidex.infrastructure.mapper.PokemonResponse;
import org.kmontano.minidex.utils.PokemonUtils;
import org.springframework.stereotype.Component;

@Component
public class PackPokemonFactory {
    private final PokemonUtils utils;
    private final EvolutionService evolutionService;

    public PackPokemonFactory(PokemonUtils utils, EvolutionService evolutionService) {
        this.utils = utils;
        this.evolutionService = evolutionService;
    }

    public PackPokemon toPackPokemon(PokemonResponse data, double shinyChance){
        boolean isShiny = utils.isShiny(shinyChance);
        PokemonSpeciesData speciesData = evolutionService.getSpeciesData(data.getSpecies().getUrl());
        return new PackPokemon()
                .setName(data.getName())
                .setImage((utils.selectImage(data, isShiny)))
                .setRarity(speciesData.getRarity())
                .setShiny(isShiny);
    }
}
