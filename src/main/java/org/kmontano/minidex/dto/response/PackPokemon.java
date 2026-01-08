package org.kmontano.minidex.dto.response;

public class PackPokemon {
    private String name;
    private String image;
    private boolean shiny;

    public PackPokemon() {
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
}
