package org.kmontano.minidex.factory;

import org.kmontano.minidex.dto.response.PackPokemon;
import org.kmontano.minidex.infrastructure.mapper.PokemonResponse;
import org.kmontano.minidex.utils.PokemonUtils;
import org.springframework.stereotype.Component;

@Component
public class PackPokemonFactory {
    private final PokemonUtils utils;

    public PackPokemonFactory(PokemonUtils utils) {
        this.utils = utils;
    }

    public PackPokemon toPackPokemon(PokemonResponse data, double shinyChance){
        boolean isShiny = utils.isShiny(shinyChance);

        return new PackPokemon().setName(data.getName()).setImage((utils.selectImage(data, isShiny))).setShiny(isShiny);
    }
}
