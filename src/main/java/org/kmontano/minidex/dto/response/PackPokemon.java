package org.kmontano.minidex.dto.response;

import org.kmontano.minidex.domain.pokemon.Rarity;

public class PackPokemon {
    private String name;
    private String image;
    private boolean shiny;
    private Rarity rarity;

    public PackPokemon() {
    }

    public PackPokemon(String name, String image, boolean shiny, Rarity rarity) {
        this.name = name;
        this.image = image;
        this.shiny = shiny;
        this.rarity = rarity;
    }

    public String getName() {
        return name;
    }

    public PackPokemon setName(String name) {
        this.name = name;
        return this;
    }

    public String getImage() {
        return image;
    }

    public PackPokemon setImage(String image) {
        this.image = image;
        return this;
    }

    public boolean isShiny() {
        return shiny;
    }

    public PackPokemon setShiny(boolean shiny) {
        this.shiny = shiny;
        return this;
    }

    public Rarity getRarity() {
        return rarity;
    }

    public PackPokemon setRarity(Rarity rarity) {
        this.rarity = rarity;
        return this;
    }
}
