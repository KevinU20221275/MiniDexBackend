package org.kmontano.minidex.infrastructure.cache;

import lombok.Data;
import org.kmontano.minidex.domain.pokemon.Rarity;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document("pack_pokemon_cache")
@Data
public class PackPokemonCache {
    @Id
    private Integer pokemonId;

    private String name;
    private String imageDefault;
    private String imageShiny;
    private Rarity rarity;
}
