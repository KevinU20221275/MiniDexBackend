package org.kmontano.minidex.dto.shared;

import org.kmontano.minidex.domain.pokemon.PokemonType;
import org.kmontano.minidex.domain.pokemon.Move;
import org.kmontano.minidex.domain.pokemon.Pokemon;

import java.util.List;

public class BattlePokemon {
    private String pokemonId;
    private String name;
    private int maxHp;
    private int currentHp;
    private int attack;
    private int defense;
    private int speed;
    private List<Move> moves;
    private List<PokemonType> types;

    public BattlePokemon() {
    }

    public BattlePokemon(Pokemon p) {
        this.pokemonId = p.getUuid();
        this.name = p.getName();
        this.maxHp = p.getStats().getHp();
        this.currentHp = p.getStats().getHp();
        this.attack = p.getStats().getAttack();
        this.defense = p.getStats().getDefense();
        this.moves = p.getMoves();
        this.types = p.getTypes();
    }

    public boolean isFainted() {
        return currentHp <= 0;
    }

    public String getPokemonId() {
        return pokemonId;
    }

    public void setPokemonId(String pokemonId) {
        this.pokemonId = pokemonId;
    }

    public List<Move> getMoves() {
        return moves;
    }

    public void setMoves(List<Move> moves) {
        this.moves = moves;
    }

    public int getSpeed() {
        return speed;
    }

    public void setSpeed(int speed) {
        this.speed = speed;
    }

    public int getDefense() {
        return defense;
    }

    public void setDefense(int defense) {
        this.defense = defense;
    }

    public int getAttack() {
        return attack;
    }

    public void setAttack(int attack) {
        this.attack = attack;
    }

    public int getCurrentHp() {
        return currentHp;
    }

    public void setCurrentHp(int currentHp) {
        this.currentHp = currentHp;
    }

    public int getMaxHp() {
        return maxHp;
    }

    public void setMaxHp(int maxHp) {
        this.maxHp = maxHp;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<PokemonType> getTypes() {
        return types;
    }

    public void setTypes(List<PokemonType> types) {
        this.types = types;
    }
}
