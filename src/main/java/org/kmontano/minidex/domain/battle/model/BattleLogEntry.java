package org.kmontano.minidex.domain.battle.model;

public class BattleLogEntry {
    private String attacker;
    private String defender;
    private String move;
    private int baseDamage;
    private double effectiveness;
    private double stab;
    private int finalDamage;
    private int defenderHpBefore;
    private int defenderHpAfter;


    public BattleLogEntry() {
    }

    public BattleLogEntry(String attacker, String defender, String move, int baseDamage, double effectiveness, double stab, int finalDamage, int defenderHpBefore, int defenderHpAfter) {
        this.attacker = attacker;
        this.defender = defender;
        this.move = move;
        this.baseDamage = baseDamage;
        this.effectiveness = effectiveness;
        this.stab = stab;
        this.finalDamage = finalDamage;
        this.defenderHpBefore = defenderHpBefore;
        this.defenderHpAfter = defenderHpAfter;
    }

    public String getAttacker() {
        return attacker;
    }

    public void setAttacker(String attacker) {
        this.attacker = attacker;
    }

    public String getDefender() {
        return defender;
    }

    public void setDefender(String defender) {
        this.defender = defender;
    }

    public String getMove() {
        return move;
    }

    public void setMove(String move) {
        this.move = move;
    }

    public int getBaseDamage() {
        return baseDamage;
    }

    public void setBaseDamage(int baseDamage) {
        this.baseDamage = baseDamage;
    }

    public double getEffectiveness() {
        return effectiveness;
    }

    public void setEffectiveness(double effectiveness) {
        this.effectiveness = effectiveness;
    }

    public double getStab() {
        return stab;
    }

    public void setStab(double stab) {
        this.stab = stab;
    }

    public int getFinalDamage() {
        return finalDamage;
    }

    public void setFinalDamage(int finalDamage) {
        this.finalDamage = finalDamage;
    }

    public int getDefenderHpBefore() {
        return defenderHpBefore;
    }

    public void setDefenderHpBefore(int defenderHpBefore) {
        this.defenderHpBefore = defenderHpBefore;
    }

    public int getDefenderHpAfter() {
        return defenderHpAfter;
    }

    public void setDefenderHpAfter(int defenderHpAfter) {
        this.defenderHpAfter = defenderHpAfter;
    }
}
