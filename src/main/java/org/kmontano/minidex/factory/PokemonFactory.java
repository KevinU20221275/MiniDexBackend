package org.kmontano.minidex.factory;

import org.kmontano.minidex.domain.pokemon.PokemonType;
import org.kmontano.minidex.domain.pokemon.Move;
import org.kmontano.minidex.domain.pokemon.Pokemon;
import org.kmontano.minidex.domain.pokemon.Stats;
import org.kmontano.minidex.infrastructure.mapper.PokemonResponse;
import org.kmontano.minidex.application.service.EvolutionService;
import org.kmontano.minidex.utils.PokemonUtils;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
public class PokemonFactory {
    private final PokemonUtils utils;
    private final MoveFactory moveFactory;
    private final EvolutionService evolutionService;

    public PokemonFactory(PokemonUtils utils, MoveFactory moveFactory, EvolutionService evolutionService){
        this.utils = utils;
        this.moveFactory = moveFactory;
        this.evolutionService = evolutionService;
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
                .setImage(utils.selectImage(data, isShiny))
                .setCanEvolve(canEvolve)
                .setNextEvolution(nextEvolution.orElse(null))
                .setStats(stats)
                .setMoves(moves)
                .setTypes(data.getTypes()
                        .stream()
                        .map(t -> PokemonType.fromApiName(t.getType().getName()))
                        .toList()
                );

        return p;
    }
}
