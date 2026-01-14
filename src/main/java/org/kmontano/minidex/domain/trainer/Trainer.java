package org.kmontano.minidex.domain.trainer;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.mapping.Document;

/**
 * Representa a un entrenador Pokémon dentro del sistema.
 * Contiene información básica, estadísticas y su lista de Pokémon asociados.
 */
@Document
@CompoundIndex(name = "unique_username", def = "{'username': 1}", unique = true)
public class Trainer {
    @Id
    private String id;

    @NotBlank(message = "El nombre no puede estar vacio")
    @Size(max = 50, message = "El nombre no puede tener más de 50 caracteres")
    private String name;

    @NotBlank(message = "El nombre de usuario no puede estar vacío")
    @Size(min = 3, max = 30, message = "El nombre de usuario debe tener entre 3 y 30 caracteres")
    private String username;

    @NotBlank(message = "La contraseña no puede estar vacía")
    @Size(min = 6, message = "La contraseña debe tener al menos 6 caracteres")
    private String password;

    @NotNull(message = "El nivel no puede ser nulo")
    @Min(value = 1, message = "El nivel mínimo es 1")
    private Integer level = 1;

    @NotNull(message = "Las monedas no pueden ser nulas")
    @Min(value = 0, message = "Las monedas no pueden ser negativas")
    private Integer coins = 250;

    private Integer wins = 0;
    private Integer loses = 0;
    private Integer xp = 0;
    private DailyPackStatus dailyPack;

    private static final int XP_PER_LEVEL = 1000;

    // Getters y setters
    public String getId() {
        return id;
    }

    public Trainer setId(String id) {
        this.id = id;
        return this;
    }

    public String getName() {
        return name;
    }

    public Trainer setName(String name) {
        this.name = name;
        return this;
    }

    public String getUsername() {
        return username;
    }

    public Trainer setUsername(String username) {
        this.username = username;
        return this;
    }

    public String getPassword() {
        return password;
    }

    public Trainer setPassword(String password) {
        this.password = password;
        return this;
    }

    public Integer getLevel() {
        return level;
    }

    public Trainer setLevel(Integer level) {
        this.level = level;
        return this;
    }

    public Integer getXp() {
        return xp;
    }

    public Trainer setXp(Integer xp) {
        this.xp = xp;
        return this;
    }

    public Integer getCoins() {
        return coins;
    }

    public Trainer setCoins(Integer coins) {
        this.coins = coins;
        return this;
    }

    public Integer getWins() {
        return wins;
    }

    public Trainer setWins(Integer wins) {
        this.wins = wins;
        return this;
    }

    public Integer getLoses() {
        return loses;
    }

    public Trainer setLoses(Integer loses) {
        this.loses = loses;
        return this;
    }

    public DailyPackStatus getDailyPack() {
        return dailyPack;
    }

    public Trainer setDailyPack(DailyPackStatus dailyPack) {
        this.dailyPack = dailyPack;
        return this;
    }

    public void addCoins(int coins){
        this.coins += coins;
    }

    public void addExperience(int experience){
        this.xp += experience;

        while (this.xp >= XP_PER_LEVEL){
            this.xp -= XP_PER_LEVEL;
            this.level++;
        }
    }

    public void subtractCoins(int amount){
        if (this.coins < amount){
            throw new IllegalStateException("No tienes monedas suficientes");
        }

        this.coins = this.coins - amount;
        addExperience(amount);
    }
}
