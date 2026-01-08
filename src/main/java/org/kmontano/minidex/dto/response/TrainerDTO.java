package org.kmontano.minidex.dto.response;

import org.kmontano.minidex.domain.trainer.DailyPackStatus;
import org.kmontano.minidex.domain.trainer.Trainer;

/**
 * Data Transfer Object (DTO) para exponer información del entrenador
 * sin incluir datos sensibles como la contraseña.
 */
public class TrainerDTO {
    private String id;
    private String name;
    private String username;
    private Integer level;
    private Integer xp;
    private Integer coins;
    private Integer wins;
    private Integer loses;
    private DailyPackStatus dailyPackStatus;

    public TrainerDTO(Trainer t){
        this.id = t.getId();
        this.name = t.getName();
        this.username = t.getUsername();
        this.level = t.getLevel();
        this.xp = t.getXp();
        this.coins = t.getCoins();
        this.wins = t.getWins();
        this.loses = t.getLoses();
        this.dailyPackStatus = t.getDailyPack();
    }

    // Getters y setters
    public String getId() {
        return id;
    }

    public void setId(String id) {
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

    public Integer getWins() {
        return wins;
    }

    public void setWins(Integer wins) {
        this.wins = wins;
    }

    public Integer getLoses() {
        return loses;
    }

    public void setLoses(Integer loses) {
        this.loses = loses;
    }

    public DailyPackStatus getDailyPackStatus() {
        return dailyPackStatus;
    }

    public void setDailyPackStatus(DailyPackStatus dailyPackStatus) {
        this.dailyPackStatus = dailyPackStatus;
    }
}
