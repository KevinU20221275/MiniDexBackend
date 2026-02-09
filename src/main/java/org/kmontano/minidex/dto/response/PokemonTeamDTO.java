package org.kmontano.minidex.dto.response;

import lombok.Data;
import org.kmontano.minidex.domain.pokemon.Pokemon;

import java.util.List;

@Data
public class PokemonTeamDTO {
    private List<PokemonDTO> team;

    public PokemonTeamDTO(List<Pokemon> team) {
        this.team = team.stream().map(PokemonDTO::new).toList();
    }
}
