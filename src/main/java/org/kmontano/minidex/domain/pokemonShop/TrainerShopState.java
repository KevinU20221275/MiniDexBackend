package org.kmontano.minidex.domain.pokemonShop;

import lombok.Data;
import org.kmontano.minidex.dto.response.PackPokemon;
import org.kmontano.minidex.exception.DomainConflictException;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDate;

@Document("trainer_shop_state")
@Data
public class TrainerShopState {
    @Id
    private String id;
    private String trainerId;
    private LocalDate shopDate;
    private boolean specialPokemonPurchased;
    private int boosterPurchasedToday;

    private static final int MAX_DAILY_PACKS = 3;

    protected TrainerShopState(){
        // For persistence
    }

    public TrainerShopState(String id, String trainerId, LocalDate shopDate, boolean specialPokemonPurchased) {
        this.id = id;
        this.trainerId = trainerId;
        this.shopDate = shopDate;
        this.specialPokemonPurchased = specialPokemonPurchased;
        this.boosterPurchasedToday = 0;
    }

    /**
     * Registers the purchase of a booster pack.
     * Throws an exception if the daily limit has been reached.
     */
    public void purchasedBooster(){
        if (this.boosterPurchasedToday >= MAX_DAILY_PACKS){
            throw new DomainConflictException("Daily booter limit reached");
        }
        this.boosterPurchasedToday++;
    }

    /**
     * Registers the purchase of the daily special Pokémon.
     * Throws an exception if it was already purchased.
     */
    public void purchasedSpecialPokemon(){
        if (this.specialPokemonPurchased){
            throw new DomainConflictException("Special pokemon already purchased today");
        }
        this.specialPokemonPurchased = true;
    }

    public int getRemainingBoosters() {
        return MAX_DAILY_PACKS - this.boosterPurchasedToday;
    }
}
