package org.kmontano.minidex.application.service;

import org.kmontano.minidex.domain.trainer.Trainer;
import org.kmontano.minidex.dto.response.BuyBoosterResponseDTO;
import org.kmontano.minidex.dto.response.PokemonStoreDTO;


public interface PokemonStoreService {
    PokemonStoreDTO getDailyStore(Trainer trainer);
    BuyBoosterResponseDTO buyBooster(Trainer trainer);
    void buySpecialPokemon(Trainer trainer);
}
