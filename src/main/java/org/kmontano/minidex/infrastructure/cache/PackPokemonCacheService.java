package org.kmontano.minidex.infrastructure.cache;

import lombok.RequiredArgsConstructor;
import org.kmontano.minidex.application.service.EvolutionService;
import org.kmontano.minidex.dto.response.PokemonSpeciesData;
import org.kmontano.minidex.infrastructure.api.PokemonApiClient;
import org.kmontano.minidex.infrastructure.mapper.PokemonResponse;
import org.kmontano.minidex.utils.PokemonUtils;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class PackPokemonCacheService {
    private final PokemonApiClient pokemonApiClient;
    private final EvolutionService evolutionService;
    private final PackPokemonCacheRepository cacheRepository;
    private final PokemonUtils utils;

    public void preloadMissingPokemon() {

        for (int id = 1; id <= 250; id++) {

            if (cacheRepository.existsById(id)) continue;

            try {
                PokemonResponse data = pokemonApiClient.getPokemonById(id);
                PokemonSpeciesData species =
                        evolutionService.getSpeciesData(data.getSpecies().getUrl());

                PackPokemonCache cache = new PackPokemonCache();
                cache.setPokemonId(id);
                cache.setName(data.getName());
                cache.setImageDefault(utils.selectImage(data, false));
                cache.setImageShiny(utils.selectImage(data, true));
                cache.setRarity(species.getRarity());

                cacheRepository.save(cache);

                Thread.sleep(200); // evitar rate limit

            } catch (Exception e) {
                System.out.println("Failed id " + id);
            }
        }
    }
}
