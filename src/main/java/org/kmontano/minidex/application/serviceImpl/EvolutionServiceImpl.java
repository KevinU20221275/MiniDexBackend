package org.kmontano.minidex.application.serviceImpl;

import org.kmontano.minidex.application.service.EvolutionService;
import org.kmontano.minidex.domain.pokemon.NextEvolution;
import org.kmontano.minidex.domain.pokemon.Rarity;
import org.kmontano.minidex.domain.pokemon.RarityMapper;
import org.kmontano.minidex.dto.response.PokemonSpeciesData;
import org.kmontano.minidex.infrastructure.mapper.ChainLink;
import org.kmontano.minidex.infrastructure.mapper.EvolutionChainResponse;
import org.kmontano.minidex.infrastructure.mapper.Species;
import org.kmontano.minidex.infrastructure.mapper.SpeciesResponse;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Optional;
import java.util.Random;

@Service
public class EvolutionServiceImpl implements EvolutionService {
    private final RestTemplate restTemplate;
    private static final Random RANDOM = new Random();

    public EvolutionServiceImpl(RestTemplate restTemplate){
        this.restTemplate = restTemplate;
    }

    @Override
    public Optional<NextEvolution> getNextEvolution(SpeciesResponse species, String currentName){
        EvolutionChainResponse chain = getEvolutionChain(species);
        return findNextEvolution(chain.getChain(), currentName);
    }

    @Override
    public PokemonSpeciesData getSpeciesData(String speciesUrl){
        SpeciesResponse species = getSpeciesFromApi(speciesUrl);
        Rarity rarity = RarityMapper.fromSpecies(species);

        return new PokemonSpeciesData(species, rarity);
    }

    private EvolutionChainResponse getEvolutionChain(SpeciesResponse species){
        return restTemplate.getForObject(
                species.getEvolution_chain().getUrl(),
                EvolutionChainResponse.class
        );
    }

    private SpeciesResponse getSpeciesFromApi(String speciesUrl){
        return restTemplate.getForObject(
                speciesUrl,
                SpeciesResponse.class
        );
    }

    private Optional<NextEvolution> findNextEvolution(ChainLink chain, String currentName){
        if (chain.getSpecies().getName().equals(currentName)){
            if (!chain.getEvolves_to().isEmpty()){
                /**
                 * if exist multiples evolutions, get one random evolution.
                 */
                List<ChainLink> evolutions = chain.getEvolves_to();
                ChainLink selected = evolutions.get(RANDOM.nextInt(evolutions.size()));

                Species nextSpecies = selected.getSpecies();
                int id = extractIdFromUrl(nextSpecies.getUrl());

                return Optional.of(
                        new NextEvolution(
                            chain.getEvolves_to().get(0).getSpecies().getName(),
                            id
                        )
                );
            }
            return Optional.empty(); // Pokemon found, but without evolution
        }

        return chain.getEvolves_to().stream()
                .map(next -> findNextEvolution(next, currentName))
                .filter(Optional::isPresent)
                .map(Optional::get)
                .findFirst();
    }

    private int extractIdFromUrl(String url) {
        String[] parts = url.split("/");
        return Integer.parseInt(parts[parts.length - 1]);
    }
}
