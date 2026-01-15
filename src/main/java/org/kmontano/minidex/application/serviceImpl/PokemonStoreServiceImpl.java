package org.kmontano.minidex.application.serviceImpl;

import org.kmontano.minidex.application.service.DailyPackService;
import org.kmontano.minidex.application.service.PokedexService;
import org.kmontano.minidex.application.service.PokemonStoreService;
import org.kmontano.minidex.application.service.TrainerService;
import org.kmontano.minidex.domain.pokemonShop.TrainerShopState;
import org.kmontano.minidex.domain.trainer.Trainer;
import org.kmontano.minidex.dto.response.BuyBoosterResponseDTO;
import org.kmontano.minidex.dto.response.PackPokemon;
import org.kmontano.minidex.dto.response.PokemonStoreDTO;
import org.kmontano.minidex.infrastructure.repository.TrainerShopStateRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.*;

@Service
public class PokemonStoreServiceImpl implements PokemonStoreService {
    private final Integer PACK_PRICE = 200;
    private final Integer SPECIAL_POKEMON_PRICE = 200;

    private TrainerShopStateRepository repository;
    private DailyPackService dailyPackService;
    private PokedexService pokedexService;
    private TrainerService trainerService;

    public PokemonStoreServiceImpl(TrainerShopStateRepository repository, DailyPackService dailyPackService, PokedexService pokedexService, TrainerService trainerService) {
        this.repository = repository;
        this.dailyPackService = dailyPackService;
        this.pokedexService = pokedexService;
        this.trainerService = trainerService;
    }

    @Override
    public PokemonStoreDTO getDailyStore(Trainer trainer){
        TrainerShopState state = getOrCreateState(trainer.getId());

        PackPokemon specialPokemon = dailyPackService.generateDailySpecial(getDailySpecialRandom(trainer.getId()));

        return new PokemonStoreDTO(
                specialPokemon,
                SPECIAL_POKEMON_PRICE,
                state.isSpecialPokemonPurchased(),
                PACK_PRICE,
                state.getRemainingBoosters()
        );
    }

    @Override
    @Transactional
    public BuyBoosterResponseDTO buyBooster(Trainer trainer){
        TrainerShopState state = getOrCreateState(trainer.getId());

        trainer.subtractCoins(PACK_PRICE);
        state.purchasedBooster();

        List<PackPokemon> pokemons = dailyPackService.getPokemonsFromBoostedPack();

        pokedexService.addPokemonsFromEnvelope(pokemons, trainer.getId());

        repository.save(state);
        trainerService.update(trainer);

        return new BuyBoosterResponseDTO(pokemons);
    }


    @Override
    public void buySpecialPokemon(Trainer trainer) {
        TrainerShopState state = getOrCreateState(trainer.getId());

        trainer.subtractCoins(SPECIAL_POKEMON_PRICE);
        state.purchasedSpecialPokemon();

        PackPokemon specialPokemon = dailyPackService.generateDailySpecial(getDailySpecialRandom(trainer.getId()));

        pokedexService.addPokemonsFromEnvelope(List.of(specialPokemon), trainer.getId());

        trainerService.update(trainer);
        repository.save(state);
    }

    private TrainerShopState getOrCreateState(String trainerId){
        LocalDate today = LocalDate.now();

        return repository.findByTrainerIdAndShopDate(trainerId, today)
                .orElseGet(() ->
                        repository.save(
                                new TrainerShopState(
                                        null,
                                        trainerId,
                                        today,
                                        false
                                )
                        ));
    }

    private Random getDailySpecialRandom(String trainerId){
        long seed = Objects.hash(
                trainerId,
                LocalDate.now(),
                "SPECIAL_POKEMON"
        );

        return new Random(seed);
    }
}
