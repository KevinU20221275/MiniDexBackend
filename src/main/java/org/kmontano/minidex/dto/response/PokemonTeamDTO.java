package org.kmontano.minidex.dto.response;

import org.kmontano.minidex.domain.pokemon.Pokemon;

import java.util.List;

public class PokemonTeamDTO {
    private List<Pokemon> team;

    public PokemonTeamDTO(List<Pokemon> team) {
        this.team = team;
    }
}
