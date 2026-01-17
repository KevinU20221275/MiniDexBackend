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

/**
 * Service implementation responsible for handling the Pokémon shop logic.
 *
 * This service coordinates trainer coin validation, shop state rules,
 * Pokémon generation, and persistence across multiple domains.
 */
@Service
public class PokemonStoreServiceImpl implements PokemonStoreService {
    /** Price of a standard booster pack */
    private final Integer PACK_PRICE = 200;

    /** Price of the daily special Pokémon */
    private final Integer SPECIAL_POKEMON_PRICE = 200;

    private final TrainerShopStateRepository repository;
    private final DailyPackService dailyPackService;
    private final PokedexService pokedexService;
    private final TrainerService trainerService;

    public PokemonStoreServiceImpl(TrainerShopStateRepository repository, DailyPackService dailyPackService, PokedexService pokedexService, TrainerService trainerService) {
        this.repository = repository;
        this.dailyPackService = dailyPackService;
        this.pokedexService = pokedexService;
        this.trainerService = trainerService;
    }

    /**
     * Returns the daily Pokémon store for the given trainer.
     *
     * The daily special Pokémon is deterministically generated per trainer and date.
     *
     * @param trainer authenticated trainer
     * @return daily store DTO with prices and remaining limits
     */
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

    /**
     * Purchases a booster pack for the trainer.
     *
     * The operation is transactional to ensure consistency between:
     * trainer coins, shop state, and pokedex updates.
     *
     * @param trainer authenticated trainer
     * @return response containing the Pokémon obtained from the booster
     */
    @Override
    @Transactional
    public BuyBoosterResponseDTO buyBooster(Trainer trainer){
        TrainerShopState state = getOrCreateState(trainer.getId());

        state.purchasedBooster();
        trainer.subtractCoins(PACK_PRICE);

        List<PackPokemon> pokemons = dailyPackService.getPokemonsFromBoostedPack();

        pokedexService.addPokemonsFromEnvelope(pokemons, trainer.getId());

        repository.save(state);
        trainerService.update(trainer);

        return new BuyBoosterResponseDTO(pokemons);
    }

    /**
     * Purchases the daily special Pokémon for the trainer.
     *
     * @param trainer authenticated trainer
     */
    @Override
    public void buySpecialPokemon(Trainer trainer) {
        TrainerShopState state = getOrCreateState(trainer.getId());

        state.purchasedSpecialPokemon();
        trainer.subtractCoins(SPECIAL_POKEMON_PRICE);

        PackPokemon specialPokemon = dailyPackService.generateDailySpecial(getDailySpecialRandom(trainer.getId()));

        pokedexService.addPokemonsFromEnvelope(List.of(specialPokemon), trainer.getId());

        trainerService.update(trainer);
        repository.save(state);
    }

    /**
     * Retrieves today's shop state for the trainer or creates a new one if none exists.
     *
     * @param trainerId trainer identifier
     * @return current daily shop state
     */
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

    /**
     * Creates a deterministic random generator for the daily special Pokémon.
     * <p>
     * Ensures:
     * - Same trainer gets the same Pokémon for the same day
     * - Different trainers get different results
     *
     * @param trainerId trainer identifier
     * @return seeded random instance
     */
    private Random getDailySpecialRandom(String trainerId){
        long seed = Objects.hash(
                trainerId,
                LocalDate.now(),
                "SPECIAL_POKEMON"
        );

        return new Random(seed);
    }
}
