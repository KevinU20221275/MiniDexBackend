package org.kmontano.minidex.dto.shared;

import lombok.Data;
import org.kmontano.minidex.domain.pokemon.*;

import java.util.ArrayList;
import java.util.List;

@Data
public class BattlePokemon {
    private String pokemonId;
    private String name;
    private int maxHp;
    private int currentHp;
    private Sprites sprites;
    private int attack;
    private int defense;
    private int speed;
    private int level;
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
        this.speed = p.getStats().getSpeed();
        this.defense = p.getStats().getDefense();
        this.level = p.getLevel();
        this.moves = p.getMoves();
        this.types = toPokemonType(p.getTypes());
        this.sprites = p.getSprites();
    }

    public boolean isFainted() {
        return currentHp <= 0;
    }

    private List<PokemonType> toPokemonType(List<PokemonTypeRef> types){
        List<PokemonType> pokemonTypes = new ArrayList<>();
        for (var t : types){
            pokemonTypes.add(PokemonType.fromApiName(t.getName()));
        }

        return pokemonTypes;
    }

    public BattlePokemon copy() {
        BattlePokemon copy = new BattlePokemon();

        copy.setPokemonId(this.pokemonId);
        copy.setName(this.name);
        copy.setLevel(this.level);

        copy.setMaxHp(this.maxHp);
        copy.setCurrentHp(this.currentHp);

        copy.setAttack(this.attack);
        copy.setDefense(this.defense);
        copy.setSpeed(this.speed);

        copy.setTypes(new ArrayList<>(this.types));
        copy.setMoves(new ArrayList<>(this.moves));
        copy.setSprites(this.sprites);

        return copy;
    }
}
