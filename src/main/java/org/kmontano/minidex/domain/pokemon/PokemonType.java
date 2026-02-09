package org.kmontano.minidex.domain.pokemon;

public enum PokemonType {
    NORMAL,
    FIRE,
    WATER,
    ELECTRIC,
    GRASS,
    ICE,
    FIGHTING,
    POISON,
    GROUND,
    FLYING,
    PSYCHIC,
    BUG,
    ROCK,
    GHOST,
    DRAGON,
    DARK,
    STEEL,
    FAIRY;

    public static PokemonType fromApiName(String apiName){
        return PokemonType.valueOf(apiName.toUpperCase());
    }
}

