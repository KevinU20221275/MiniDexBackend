package org.kmontano.minidex.domain.trainer;

import org.kmontano.minidex.exception.DomainValidationException;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.mapping.Document;

@Document
@CompoundIndex(name = "unique_username", def = "{'username': 1}", unique = true)
public class Trainer {
    @Id
    private String id;

    private String name;
    private String username;
    private String password;
    private Integer level = 1;
    private Integer coins = 250;
    private Integer wins = 0;
    private Integer loses = 0;
    private Integer xp = 0;
    private DailyPackStatus dailyPack;

    private static final int XP_PER_LEVEL = 1000;

    /* private constructor: only Builder can create Trainers */
    private Trainer(Builder builder){
        this.name = builder.name;
        this.username = builder.username;
        this.password = builder.password;
        this.level = builder.level;
        this.coins = builder.coins;
        this.wins = builder.wins;
        this.loses = builder.loses;
        this.xp = builder.xp;
        this.dailyPack = builder.dailyPack;
    }

    /* =========================
       ===== Domain Logic ======
       ========================= */

    public void addCoins(int coins){
        if (coins < 0){
            throw new DomainValidationException("Coins cannot be negative");
        }
        this.coins += coins;
    }

    public void addXp(int amount){
        if (amount < 0) {
            throw new DomainValidationException("XP amount cannot be negative");
        }

        this.xp += amount;

        while (this.xp >= XP_PER_LEVEL){
            this.xp -= XP_PER_LEVEL;
            this.level++;
        }
    }

    public void subtractCoins(int amount){
        if (this.coins < amount){
            throw new DomainValidationException("You don't have enough coins");
        }

        this.coins = this.coins - amount;
        addXp(amount);
    }

    public void onWin(){
        this.wins++;
    }

    public void onLose(){
        this.loses++;
    }

    public void updateNameAndUsername(String name, String username){
        this.name = name;
        this.username = username;
    }

    /* =========================
       ===== Getters  ======
       ========================= */

    public String getId() { return id; }
    public String getName() { return name; }
    public String getUsername() { return username; }
    public int getLevel() { return level; }
    public int getCoins() { return coins; }
    public int getWins() { return wins; }
    public int getLoses() { return loses; }
    public int getXp() { return xp; }
    public DailyPackStatus getDailyPack() { return dailyPack; }

    /* =========================
       ======= Builder =========
       ========================= */

    public static class Builder {

        private String name;
        private String username;
        private String password;

        private int level = 1;
        private int coins = 250;
        private int wins = 0;
        private int loses = 0;
        private int xp = 0;

        private DailyPackStatus dailyPack;

        public Builder name(String name) {
            this.name = name;
            return this;
        }

        public Builder username(String username) {
            this.username = username;
            return this;
        }

        public Builder password(String password) {
            this.password = password;
            return this;
        }

        public Builder dailyPack(DailyPackStatus dailyPack) {
            this.dailyPack = dailyPack;
            return this;
        }

        /* validate FINAL domain */
        public Trainer build() {
            if (name == null || name.isBlank()) {
                throw new DomainValidationException("Trainer name is required");
            }

            if (username == null || username.isBlank()) {
                throw new DomainValidationException("Username is required");
            }

            if (password == null || password.isBlank()) {
                throw new DomainValidationException("Password is required");
            }

            if (level < 1) {
                throw new DomainValidationException("Level must be at least 1");
            }

            if (coins < 0) {
                throw new DomainValidationException("Coins cannot be negative");
            }

            return new Trainer(this);
        }
    }
}
