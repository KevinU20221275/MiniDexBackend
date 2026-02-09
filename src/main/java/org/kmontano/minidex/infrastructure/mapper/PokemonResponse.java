package org.kmontano.minidex.infrastructure.mapper;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PokemonResponse {
    private Integer id;
    private String name;
    private List<StatSlot> stats;
    private List<TypeSlot> types;
    private List<MoveSlot> moves;
    private Sprites sprites;
    private Species species;
}
