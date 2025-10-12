package org.kmontano.minidex.dto;

import org.kmontano.minidex.models.Pokemon;
import org.kmontano.minidex.models.PokemonType;

import java.util.List;
import java.util.stream.Collectors;

/**
 * DTO para enviar información de un Pokémon al cliente.
 * Incluye estadísticas y tipos, excluyendo relaciones con entrenadores.
 */
public class PokemonDTO {
    private Long id;
    private String name;
    private String image;
    private int hp, attack, defense, speed;
    private List<String> types;

    public PokemonDTO(Pokemon p) {
        this.id = p.getId();
        this.name = p.getName();
        this.image = p.getImage();
        this.hp = p.getHp();
        this.attack = p.getAttack();
        this.defense = p.getDefense();
        this.speed = p.getSpeed();
        this.types = p.getTypes().stream().map(PokemonType::getName).collect(Collectors.toList());
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public int getHp() {
        return hp;
    }

    public void setHp(int hp) {
        this.hp = hp;
    }

    public int getAttack() {
        return attack;
    }

    public void setAttack(int attack) {
        this.attack = attack;
    }

    public int getDefense() {
        return defense;
    }

    public void setDefense(int defense) {
        this.defense = defense;
    }

    public int getSpeed() {
        return speed;
    }

    public void setSpeed(int speed) {
        this.speed = speed;
    }

    public List<String> getTypes() {
        return types;
    }

    public void setTypes(List<String> types) {
        this.types = types;
    }
}
