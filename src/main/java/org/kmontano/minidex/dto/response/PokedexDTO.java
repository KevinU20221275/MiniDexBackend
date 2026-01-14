package org.kmontano.minidex.dto.response;

import lombok.Data;
import org.kmontano.minidex.domain.pokedex.Pokedex;
import org.kmontano.minidex.domain.pokemon.Pokemon;

import java.util.List;

@Data
public class PokedexDTO {
    private List<Pokemon> pokemonTeam;
    private List<Pokemon> pokedex;

    public PokedexDTO(Pokedex p){
        pokemonTeam = p.getPokemonTeamExpanded();
        pokedex = p.getPokemons();
    }
}
