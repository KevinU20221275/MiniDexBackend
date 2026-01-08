package org.kmontano.minidex.dto.response;

import org.kmontano.minidex.domain.pokedex.Pokedex;
import org.kmontano.minidex.domain.pokemon.Pokemon;

import java.util.List;

public class PokedexDTO {
    private List<Pokemon> pokemonTeam;
    private List<Pokemon> pokedex;

    public PokedexDTO(Pokedex p){
        pokemonTeam = p.getPokemonTeamExpanded();
        pokedex = p.getPokemons();
    }

    public List<Pokemon> getPokemonTeam() {
        return pokemonTeam;
    }

    public void setPokemonTeam(List<Pokemon> pokemonTeam) {
        this.pokemonTeam = pokemonTeam;
    }

    public List<Pokemon> getPokedex() {
        return pokedex;
    }

    public void setPokedex(List<Pokemon> pokedex) {
        this.pokedex = pokedex;
    }
}
