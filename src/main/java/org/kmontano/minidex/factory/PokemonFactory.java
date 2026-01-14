package org.kmontano.minidex.factory;

import org.kmontano.minidex.application.service.PokemonTypeCacheService;
import org.kmontano.minidex.domain.pokemon.*;
import org.kmontano.minidex.infrastructure.mapper.PokemonResponse;
import org.kmontano.minidex.application.service.EvolutionService;
import org.kmontano.minidex.utils.PokemonUtils;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Component
public class PokemonFactory {
    private final PokemonUtils utils;
    private final MoveFactory moveFactory;
    private final EvolutionService evolutionService;
    private final PokemonTypeCacheService typeCacheService;

    public PokemonFactory(PokemonUtils utils, MoveFactory moveFactory, EvolutionService evolutionService, PokemonTypeCacheService typeCacheService){
        this.utils = utils;
        this.moveFactory = moveFactory;
        this.evolutionService = evolutionService;
        this.typeCacheService = typeCacheService;
    }

    public Pokemon toFullPokemon(PokemonResponse data, boolean isShiny){
        Stats stats = utils.mapStats(data);
        List<String> movesNames = utils.getRandomMoves(data, 4);
        List<Move> moves = movesNames.stream()
                .map(moveFactory::fromMoveName)
                .toList();

        Optional<String> nextEvolution = evolutionService.getNextEvolutionName(data.getSpecies().getUrl(), data.getName());
        boolean canEvolve = nextEvolution.isPresent();

        Pokemon p = new Pokemon();
        p.setName(data.getName())
                .setNumPokedex(data.getId())
                .setLevel(1)
                .setShiny(isShiny)
                .setSprites(utils.selectSprites(data, isShiny))
                .setCanEvolve(canEvolve)
                .setNextEvolution(nextEvolution.orElse(null))
                .setStats(stats)
                .setMoves(moves)
                .setTypes(getTypesWithIcon(data));

        return p;
    }

    public List<PokemonTypeRef> getTypesWithIcon(PokemonResponse data) {

        List<PokemonTypeRef> types = new ArrayList<>();

        for (var t : data.getTypes()) {

            PokemonTypeCache cache =
                    typeCacheService.getType(
                            t.getType().getUrl()
                    );

            PokemonTypeRef ref = new PokemonTypeRef();
            ref.setName(cache.getName());
            ref.setIconUrl(cache.getIconUrl());

            types.add(ref);
        }

        return types;
    }
}
