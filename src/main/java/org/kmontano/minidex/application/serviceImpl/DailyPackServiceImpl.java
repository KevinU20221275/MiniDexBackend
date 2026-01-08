package org.kmontano.minidex.application.serviceImpl;

import org.kmontano.minidex.application.service.DailyPackService;
import org.kmontano.minidex.dto.response.PackPokemon;
import org.kmontano.minidex.domain.trainer.DailyPackStatus;
import org.kmontano.minidex.domain.trainer.Envelope;
import org.kmontano.minidex.dto.response.SpecialPokemonDTO;
import org.kmontano.minidex.factory.PackPokemonFactory;
import org.kmontano.minidex.infrastructure.mapper.PokemonResponse;
import org.kmontano.minidex.infrastructure.api.PokemonApiClient;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

@Service
public class DailyPackServiceImpl implements DailyPackService {
    private static final int TOTAL_DAILY_POKEMONS = 9;
    private static final int MAX_ENABLE_ENVELOPES = 3;
    private static final float SHINY_RATE = 0.05f;

    private final PokemonApiClient pokeApiClient;
    private final PackPokemonFactory packPokemonFactory;

    public DailyPackServiceImpl(PokemonApiClient pokeApiClient, PackPokemonFactory packPokemonFactory) {
        this.pokeApiClient = pokeApiClient;
        this.packPokemonFactory = packPokemonFactory;
    }

    @Override
    public DailyPackStatus initialize() {

        DailyPackStatus status = new DailyPackStatus();
        status.setNumEnvelopes(MAX_ENABLE_ENVELOPES);
        status.setLastResetDate(LocalDate.now());
        status.setEnvelopes(buildEnvelopes(generateDailyPackPokemons()));

        return status;
    }

    @Override
    public List<PackPokemon> generateDailyPackPokemons(){
        List<PackPokemon> pokemons = new ArrayList<>();

        for (int i =0; i < TOTAL_DAILY_POKEMONS; i++){
            pokemons.add(getRandomPackPokemon());
        }

        return pokemons;
    }

    @Override
    public PackPokemon getRandomPackPokemon(){
        int randonId = ThreadLocalRandom.current().nextInt(1, 251);

        PokemonResponse response = pokeApiClient.getPokemonById(randonId);

        return packPokemonFactory.toPackPokemon(response, SHINY_RATE);
    }

    @Override
    public PackPokemon getRandomPackPokemon(Double customShinyRate){
        int randonId = ThreadLocalRandom.current().nextInt(1, 251);

        PokemonResponse response = pokeApiClient.getPokemonById(randonId);

        return packPokemonFactory.toPackPokemon(response, customShinyRate);
    }

    @Override
    public List<Envelope> buildEnvelopes(List<PackPokemon> pokemons){
        List<Envelope> envelopes = new ArrayList<>();

        for (int i = 0; i < MAX_ENABLE_ENVELOPES; i++){
            envelopes.add(new Envelope()
                    .setId("slot_"+(i+1))
                    .setOpened(false)
                    .setPokemons(pokemons.subList(i * 3, (i + 1) * 3)));
        }

        return envelopes;
    }

    @Override
    public DailyPackStatus resetIfNeeded(DailyPackStatus status){
        LocalDate today = LocalDate.now();

        if (status.getLastResetDate() == null || status.getLastResetDate().isBefore(today)) {
            status.setLastResetDate(today);
            status.setNumEnvelopes(3);
            status.setEnvelopes(buildEnvelopes(generateDailyPackPokemons()));
        }

        return status;
    }

    @Override
    public List<PackPokemon> getPokemonsFromBoostedPack() {
        List<PackPokemon> pokemons = new ArrayList<>();

        for (int i =0; i < 3; i++){
            pokemons.add(getRandomPackPokemon());
        }

        return pokemons;
    }

    @Override
    public PackPokemon generateDailySpecial(Random random) {
        int pokedexNumber = random.nextInt(151) + 1;

        PokemonResponse response = pokeApiClient.getPokemonById(pokedexNumber);

        return packPokemonFactory.toPackPokemon(response, SHINY_RATE);
    }
}
