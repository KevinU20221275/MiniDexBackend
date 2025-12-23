package org.kmontano.minidex.domain.pokemon;

public class Stats {
    private Integer hp;
    private Integer attack;
    private Integer defense;
    private Integer speed;

    public Stats(){}

    public Integer getHp() {
        return hp;
    }

    public Stats setHp(Integer hp) {
        this.hp = hp;
        return this;
    }

    public Integer getAttack() {
        return attack;
    }

    public Stats setAttack(Integer attack) {
        this.attack = attack;
        return this;
    }

    public Integer getDefense() {
        return defense;
    }

    public Stats setDefense(Integer defense) {
        this.defense = defense;
        return this;
    }

    public Integer getSpeed() {
        return speed;
    }

    public Stats setSpeed(Integer speed) {
        this.speed = speed;
        return this;
    }
}