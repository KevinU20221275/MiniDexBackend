package org.kmontano.minidex.application.serviceImpl;

import org.kmontano.minidex.application.service.DailyPackService;
import org.kmontano.minidex.domain.pokemon.Rarity;
import org.kmontano.minidex.dto.response.DailySpecialPokemonInfoDTO;
import org.kmontano.minidex.dto.response.PackPokemon;
import org.kmontano.minidex.exception.ResourceNotFoundException;
import org.kmontano.minidex.factory.PackPokemonFactory;
import org.kmontano.minidex.infrastructure.cache.PackPokemonCache;
import org.kmontano.minidex.infrastructure.cache.PackPokemonCacheRepository;
import org.kmontano.minidex.infrastructure.mapper.PokemonResponse;
import org.kmontano.minidex.infrastructure.api.PokemonApiClient;
import org.kmontano.minidex.infrastructure.mapper.StatSlot;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

@Service
public class DailyPackServiceImpl implements DailyPackService {
    private static final int TOTAL_DAILY_POKEMONS_IN_PACK = 3;
    private static final int MAX_ENABLE_ENVELOPES = 3;
    private static final float SHINY_RATE = 0.05f;
    private static final double SHINY_RATE_IN_BOOSTER_PACK = 0.15f;

    private final PokemonApiClient pokeApiClient;
    private final PackPokemonFactory packPokemonFactory;
    private final PackPokemonCacheRepository cacheRepository;

    public DailyPackServiceImpl(PokemonApiClient pokeApiClient, PackPokemonFactory packPokemonFactory, PackPokemonCacheRepository cacheRepository) {
        this.pokeApiClient = pokeApiClient;
        this.packPokemonFactory = packPokemonFactory;
        this.cacheRepository = cacheRepository;
    }

    @Override
    public List<PackPokemon> generateDailyPackPokemons(){
        List<PackPokemon> pokemons = new ArrayList<>();

        for (int i =0; i < TOTAL_DAILY_POKEMONS_IN_PACK; i++){
            pokemons.add(getRandomPackPokemon());
        }

        return pokemons;
    }

    @Override
    public PackPokemon getRandomPackPokemon(){
        int randonId = ThreadLocalRandom.current().nextInt(1, 251);

        PackPokemonCache cache = cacheRepository.findById(randonId).orElseThrow();

        return packPokemonFactory.toPackPokemon(cache, SHINY_RATE);
    }

    @Override
    public PackPokemon getRandomPackPokemon(Double customShinyRate){
        int randonId = ThreadLocalRandom.current().nextInt(1, 251);

        PackPokemonCache cache = cacheRepository.findById(randonId).orElseThrow();

        return packPokemonFactory.toPackPokemon(cache, customShinyRate);
    }


    @Override
    public List<PackPokemon> getPokemonsFromBoostedPack() {
        List<PackPokemon> pokemons = new ArrayList<>();

        for (int i =0; i < 3; i++){
            pokemons.add(getRandomPackPokemon(SHINY_RATE_IN_BOOSTER_PACK));
        }

        return pokemons;
    }

    @Override
    public DailySpecialPokemonInfoDTO generateDailySpecial(Random random) {
        List<Integer> ids = cacheRepository.findAll()
                .stream()
                .map(PackPokemonCache::getPokemonId)
                .toList();

        int randomId = ids.get(random.nextInt(ids.size()));

        PokemonResponse response = pokeApiClient.getPokemonById(randomId);

        PackPokemonCache cache = cacheRepository.findById(randomId).orElseThrow(() -> new ResourceNotFoundException("Pokemon not found"));

        return new DailySpecialPokemonInfoDTO(calculatePriceByRarity(cache.getRarity(), response), packPokemonFactory.toPackPokemon(cache, 1));
    }

    private int calculatePriceByRarity(Rarity rarity, PokemonResponse data){
        int base = switch (rarity){
            case COMMON -> 50;
            case UNCOMMON -> 75;
            case EPIC -> 100;
            case RARE -> 200;
            case LEGENDARY -> 300;
        };

        int powerScore = data.getStats().stream().mapToInt(StatSlot::getBaseStat).sum() / 3;

        return base + powerScore;
    }
}
