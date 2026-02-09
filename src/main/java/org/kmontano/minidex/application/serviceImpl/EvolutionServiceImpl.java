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

import java.util.Optional;

@Service
public class EvolutionServiceImpl implements EvolutionService {
    private final RestTemplate restTemplate;

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
                 * Si existen múltiples evoluciones, se selecciona la primera por defecto.
                 */
                Species nextSpecies = chain.getEvolves_to().get(0).getSpecies();

                int id = extractIdFromUrl(nextSpecies.getUrl());
                return Optional.of(
                        new NextEvolution(
                            chain.getEvolves_to().get(0).getSpecies().getName(),
                            id
                        )
                );
            }
            return Optional.empty();
        }

        for (ChainLink next : chain.getEvolves_to()){
            Optional<NextEvolution> found = findNextEvolution(next, currentName);
            if (found.isPresent()){
                return  found;
            }
        }

        return Optional.empty();
    }

    private int extractIdFromUrl(String url) {
        String[] parts = url.split("/");
        return Integer.parseInt(parts[parts.length - 1]);
    }
}
