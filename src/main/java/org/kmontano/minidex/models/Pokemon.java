package org.kmontano.minidex.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.util.ArrayList;
import java.util.List;

/**
 * Entidad que representa un Pokémon.
 * Contiene atributos básicos como nombre, estadísticas y tipos.
 */
@Entity
@Table(name = "pokemons")
public class Pokemon {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "El nombre del Pokémon es obligatorio")
    private String name;

    @NotBlank(message = "La imagen del Pokémon es obligatoria")
    private String image;

    @NotNull(message = "El HP del Pokémon es obligatorio")
    private Integer hp;

    @NotNull(message = "La defensa del Pokémon es obligatoria")
    private Integer defense;

    @NotNull(message = "El ataque del Pokémon es obligatorio")
    private Integer attack;

    @NotNull(message = "La velocidad del Pokémon es obligatoria")
    private Integer speed;

    private Integer level;

    @ManyToMany
    @JoinTable(
            name = "pokemon_type",
            joinColumns = @JoinColumn(name = "pokemon_id"),
            inverseJoinColumns = @JoinColumn(name = "type_id")
    )
    private List<PokemonType> types = new ArrayList<>();

    @JsonIgnore
    @ManyToMany(mappedBy = "pokemons")
    private List<Trainer> trainers = new ArrayList<>();

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

    public List<PokemonType> getTypes() {
        return types;
    }

    public void setTypes(List<PokemonType> types) {
        this.types = types;
    }

    public Integer getLevel() {
        return level;
    }

    public void setLevel(Integer level) {
        this.level = level;
    }

    public Integer getHp() {
        return hp;
    }

    public void setHp(Integer hp) {
        this.hp = hp;
    }

    public Integer getDefense() {
        return defense;
    }

    public void setDefense(Integer defense) {
        this.defense = defense;
    }

    public Integer getAttack() {
        return attack;
    }

    public void setAttack(Integer attack) {
        this.attack = attack;
    }

    public Integer getSpeed() {
        return speed;
    }

    public void setSpeed(Integer speed) {
        this.speed = speed;
    }
}
