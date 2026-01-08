package org.kmontano.minidex.domain.pokemonShop;

import lombok.Data;
import org.kmontano.minidex.dto.response.PackPokemon;
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
    private int boosterPusrchasedToday;

    public TrainerShopState(String id, String trainerId, LocalDate shopDate, boolean specialPokemonPurchased, int boosterPusrchasedToday) {
        this.id = id;
        this.trainerId = trainerId;
        this.shopDate = shopDate;
        this.specialPokemonPurchased = specialPokemonPurchased;
        this.boosterPusrchasedToday = boosterPusrchasedToday;
    }

    public void onPurchasedBooster(){
        this.boosterPusrchasedToday++;
    }

    public void onPurchasedSpecialPokemon(){
        this.specialPokemonPurchased = true;
    }
}
