package org.kmontano.minidex.domain.pokemon;

import lombok.Data;

@Data
public class NextEvolution {
    private String name;
    private Integer numPokedex;

    public NextEvolution() {

    }

    public NextEvolution(String name, Integer numPokedex) {
        this.name = name;
        this.numPokedex = numPokedex;
    }
}
