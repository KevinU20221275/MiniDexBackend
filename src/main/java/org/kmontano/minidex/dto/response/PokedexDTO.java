package org.kmontano.minidex.dto.response;

import lombok.Data;
import org.kmontano.minidex.domain.pokedex.Pokedex;

import java.util.List;

@Data
public class PokedexDTO {
    private List<PokemonDTO> pokemonTeam;
    private List<PokemonDTO> pokedex;

    public PokedexDTO(Pokedex p){
        pokemonTeam = p.getPokemonTeamExpanded().stream().map(PokemonDTO::new).toList();
        pokedex = p.getPokemons().stream().map(PokemonDTO::new).toList();
    }
}