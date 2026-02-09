package org.kmontano.minidex.domain.pokemon;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;

@Data
@Document("type_cache")
public class PokemonTypeCache {
    @Id
    private Integer id;
    private String name;
    private String iconUrl;
    @Indexed(expireAfter = "30d")
    private Instant cacheAt;
}
