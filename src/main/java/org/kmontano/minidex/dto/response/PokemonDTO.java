package org.kmontano.minidex.dto.response;

import lombok.Data;
import org.kmontano.minidex.domain.pokemon.*;

import java.util.List;

/**
 * DTO para enviar información de un Pokémon al cliente.
 * Incluye estadísticas y tipos, excluyendo relaciones con entrenadores.
 */
@Data
public class PokemonDTO {
    private String uuid;
    private Integer numPokedex;
    private String name;
    private Rarity rarity;
    private Sprites sprites;
    private Boolean shiny;
    private Integer level;
    private NextEvolution nextEvolution;
    private Stats stats;
    private List<PokemonTypeRef> types;
    private List<Move> moves;
    private boolean canEvolve;

    public PokemonDTO(Pokemon p){
        this.uuid = p.getUuid();
        this.numPokedex = p.getNumPokedex();
        this.name = p.getName();
        this.rarity = p.getRarity();
        this.sprites = p.getSprites();
        this.shiny = p.getShiny();
        this.level = p.getLevel();
        this.nextEvolution = p.getNextEvolution();
        this.stats = p.getStats();
        this.types = p.getTypes();
        this.moves = p.getMoves();
        this.canEvolve = p.canEvolve();
    }
}
