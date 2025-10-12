package org.kmontano.minidex.dto;

import org.kmontano.minidex.models.Trainer;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Data Transfer Object (DTO) para exponer información del entrenador
 * sin incluir datos sensibles como la contraseña.
 */
public class TrainerDTO {
    private Long id;
    private String name;
    private String username;
    private Integer level;
    private Integer coins;
    private List<PokemonDTO> pokedex;

    public TrainerDTO(Trainer t){
        this.id = t.getId();
        this.name = t.getName();
        this.username = t.getUsername();
        this.level = t.getLevel();
        this.coins = t.getCoins();

        // Evita null pointer si el trainer no tiene pokemons cargados (por ejemplo, en login)
        if (t.getPokemons() != null) {
            this.pokedex = t.getPokemons().stream()
                    .map(PokemonDTO::new)
                    .collect(Collectors.toList());
        }
    }

    // Getters y setters
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

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public Integer getLevel() {
        return level;
    }

    public void setLevel(Integer level) {
        this.level = level;
    }

    public Integer getCoins() {
        return coins;
    }

    public void setCoins(Integer coins) {
        this.coins = coins;
    }

    public List<PokemonDTO> getPokedex() {
        return pokedex;
    }

    public void setPokedex(List<PokemonDTO> pokedex) {
        this.pokedex = pokedex;
    }
}
