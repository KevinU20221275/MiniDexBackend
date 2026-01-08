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
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.util.*;


public class PokemonStoreServiceImpl implements PokemonStoreService {
    private final Integer PACK_PRICE = 200;
    private final Integer SPECIAL_POKEMON_PRICE = 200;
    private final Integer MAX_DAILY_PACKS = 3;

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
                MAX_DAILY_PACKS - state.getBoosterPusrchasedToday()
        );
    }

    @Override
    public BuyBoosterResponseDTO buyBooster(Trainer trainer){
        TrainerShopState state = getOrCreateState(trainer.getId());

        if (state.getBoosterPusrchasedToday() >= MAX_DAILY_PACKS){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Ya compraste el numero maximo de sobres, vuelve ma;ana");
        }

        if (trainer.getCoins() < PACK_PRICE){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "No tienes monedas suficientes");
        }

        List<PackPokemon> pokemons = dailyPackService.getPokemonsFromBoostedPack();

        pokedexService.addPokemonsFromEnvelope(pokemons, trainer.getId());

        trainer.subtractCoins(PACK_PRICE);
        state.onPurchasedBooster();

        repository.save(state);
        trainerService.update(trainer);

        return new BuyBoosterResponseDTO(pokemons);
    }


    @Override
    public TrainerShopState buySpecialPokemon(Trainer trainer) {
        if (trainer.getCoins() < SPECIAL_POKEMON_PRICE){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "No tienes monedas suficientes");
        }

        TrainerShopState state = getOrCreateState(trainer.getId());

        if (state.isSpecialPokemonPurchased()){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "El pokemon especial ya fue comprado");
        }

        PackPokemon specialPokemon = dailyPackService.generateDailySpecial(getDailySpecialRandom(trainer.getId()));

        pokedexService.addPokemonsFromEnvelope(List.of(specialPokemon), trainer.getId());

        trainer.subtractCoins(SPECIAL_POKEMON_PRICE);
        trainerService.update(trainer);

        state.onPurchasedSpecialPokemon();
        return repository.save(state);
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
                                        false,
                                        0
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
