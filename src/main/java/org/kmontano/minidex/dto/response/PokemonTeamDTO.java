package org.kmontano.minidex.dto.response;

import lombok.Data;
import org.kmontano.minidex.domain.pokemon.Pokemon;

import java.util.List;

@Data
public class PokemonTeamDTO {
    private List<Pokemon> team;

    public PokemonTeamDTO(List<Pokemon> team) {
        this.team = team;
    }
}
