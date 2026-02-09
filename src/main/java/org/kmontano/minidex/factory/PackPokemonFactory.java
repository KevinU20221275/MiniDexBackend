package org.kmontano.minidex.factory;

import org.kmontano.minidex.dto.response.PackPokemon;
import org.kmontano.minidex.infrastructure.cache.PackPokemonCache;
import org.kmontano.minidex.utils.PokemonUtils;
import org.springframework.stereotype.Component;

@Component
public class PackPokemonFactory {
    private final PokemonUtils utils;

    public PackPokemonFactory(PokemonUtils utils) {
        this.utils = utils;
    }

    public PackPokemon toPackPokemon(PackPokemonCache cache, double shinyChance){
        boolean isShiny = utils.isShiny(shinyChance);

        return new PackPokemon()
                .setName(cache.getName())
                .setImage(isShiny ? cache.getImageShiny() : cache.getImageDefault())
                .setRarity(cache.getRarity())
                .setShiny(isShiny);
    }
}
