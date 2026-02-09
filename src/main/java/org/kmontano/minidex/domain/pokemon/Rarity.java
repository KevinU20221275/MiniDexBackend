package org.kmontano.minidex.domain.pokemon;

public enum Rarity {
    COMMON(5),
    UNCOMMON(10),
    RARE(20),
    EPIC(40),
    LEGENDARY(70);

    private final int baseCoins;

    Rarity(int baseCoins) {
        this.baseCoins = baseCoins;
    }

    public int getBaseCoins() {
        return baseCoins;
    }
}

