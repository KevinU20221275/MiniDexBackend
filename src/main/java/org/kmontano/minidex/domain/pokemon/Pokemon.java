package org.kmontano.minidex.domain.pokemon;

import java.util.List;
import java.util.UUID;

/**
 * Entidad que representa un Pokémon.
 * Contiene atributos básicos como nombre, estadísticas y tipos.
 */
public class Pokemon {
    private String uuid;
    private Integer numPokedex;
    private String name;
    private String image;
    private Boolean shiny;
    private Integer level;
    private Boolean canEvolve;
    private String nextEvolution;
    private String speciesUrl;
    private Stats stats;
    private List<PokemonType> types;
    private List<Move> moves;



    public Pokemon(){
        this.uuid = UUID.randomUUID().toString();
    }

    public Pokemon(Integer numPokedex, String name, String image, Boolean shiny, Integer level, Boolean canEvolve, Stats stats, List<PokemonType> types, List<Move> moves) {
        this.uuid = UUID.randomUUID().toString();
        this.numPokedex = numPokedex;
        this.name = name;
        this.image = image;
        this.shiny = shiny;
        this.level = level;
        this.canEvolve = canEvolve;
        this.stats = stats;
        this.types = types;
        this.moves = moves;
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public Integer getNumPokedex() {
        return numPokedex;
    }

    public Pokemon setNumPokedex(Integer numPokedex) {
        this.numPokedex = numPokedex;
        return this;
    }

    public String getName() {
        return name;
    }

    public Pokemon setName(String name) {
        this.name = name;
        return this;
    }

    public String getImage() {
        return image;
    }

    public Pokemon setImage(String image) {
        this.image = image;
        return this;
    }

    public Integer getLevel() {
        return level;
    }

    public Pokemon setLevel(Integer level) {
        this.level = level;
        return this;
    }

    public Boolean getShiny() {
        return shiny;
    }

    public Pokemon setShiny(Boolean shiny) {
        this.shiny = shiny;
        return this;
    }

    public Boolean getCanEvolve() {
        return canEvolve;
    }

    public Pokemon setCanEvolve(Boolean canEvolve) {
        this.canEvolve = canEvolve;
        return this;
    }

    public String getNextEvolution() {
        return nextEvolution;
    }

    public Pokemon setNextEvolution(String nextEvolution) {
        this.nextEvolution = nextEvolution;
        return this;
    }

    public String getSpeciesUrl() {
        return speciesUrl;
    }

    public Pokemon setSpeciesUrl(String speciesUrl) {
        this.speciesUrl = speciesUrl;
        return this;
    }

    public Stats getStats() {
        return stats;
    }

    public Pokemon setStats(Stats stats) {
        this.stats = stats;
        return this;
    }

    public List<PokemonType> getTypes() {
        return types;
    }

    public Pokemon setTypes(List<PokemonType> types) {
        this.types = types;
        return this;
    }

    public List<Move> getMoves() {
        return moves;
    }

    public Pokemon setMoves(List<Move> moves) {
        this.moves = moves;
        return this;
    }
}
