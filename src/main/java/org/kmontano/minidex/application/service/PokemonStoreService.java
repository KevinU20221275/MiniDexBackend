package org.kmontano.minidex.application.service;

import org.kmontano.minidex.domain.pokemonShop.PokemonStore;
import org.kmontano.minidex.domain.pokemonShop.TrainerShopState;
import org.kmontano.minidex.domain.trainer.Trainer;
import org.kmontano.minidex.dto.response.BuyBoosterResponseDTO;
import org.kmontano.minidex.dto.response.PokemonStoreDTO;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public interface PokemonStoreService {
    PokemonStoreDTO getDailyStore(Trainer trainer);
    BuyBoosterResponseDTO buyBooster(Trainer trainer);
    TrainerShopState buySpecialPokemon(Trainer trainer);
}
