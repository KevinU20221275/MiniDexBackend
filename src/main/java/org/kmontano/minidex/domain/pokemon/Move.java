package org.kmontano.minidex.domain.pokemon;

import lombok.Data;

@Data
public class Move {
    private String moveName;        // Ember, Tackle, Thunderbolt
    private PokemonType type;        // fire, water, normal...
    private Integer power;
    private Integer accuracy;

    public Move() {}

    public Move(String moveName, PokemonType type, Integer power, Integer accuracy) {
        this.moveName = moveName;
        this.type = type;
        this.power = power;
        this.accuracy = accuracy;
    }

    public String getMoveName() {
        return moveName;
    }

    public void setMoveName(String moveName) {
        this.moveName = moveName;
    }

    public PokemonType getType() {
        return type;
    }

    public void setType(PokemonType type) {
        this.type = type;
    }

    public Integer getPower() {
        return power;
    }

    public void setPower(Integer power) {
        this.power = power;
    }

    public Integer getAccuracy() {
        return accuracy;
    }

    public void setAccuracy(Integer accuracy) {
        this.accuracy = accuracy;
    }
}