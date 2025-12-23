package org.kmontano.minidex.domain.trainer;

import org.kmontano.minidex.domain.pokemon.PackPokemon;

import java.util.List;

public class Envelope {
    private String id;
    private boolean opened;
    private List<PackPokemon> pokemons;

    public Envelope() {
    }

    public String getId() {
        return id;
    }

    public Envelope setId(String id) {
        this.id = id;
        return this;
    }

    public boolean isOpened() {
        return opened;
    }

    public Envelope setOpened(boolean opened) {
        this.opened = opened;
        return this;
    }

    public List<PackPokemon> getPokemons() {
        return pokemons;
    }

    public Envelope setPokemons(List<PackPokemon> pokemons) {
        this.pokemons = pokemons;
        return this;
    }
}
