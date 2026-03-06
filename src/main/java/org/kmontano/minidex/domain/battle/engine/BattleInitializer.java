package org.kmontano.minidex.domain.battle.engine;

import org.kmontano.minidex.application.service.PokedexService;
import org.kmontano.minidex.domain.battle.model.BattleContext;
import org.kmontano.minidex.domain.pokedex.Pokedex;
import org.kmontano.minidex.dto.shared.BattlePokemon;
import org.kmontano.minidex.domain.enemy.factory.EnemyTeamFactory;
import org.kmontano.minidex.utils.PokemonUtils;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Component
public class BattleInitializer {
    private final PokedexService pokedexService;
    private final PokemonUtils pokemonUtils;
    private final EnemyTeamFactory enemyTeamFactory;

    public BattleInitializer(PokedexService pokedexService, PokemonUtils pokemonUtils, EnemyTeamFactory enemyTeamFactory) {
        this.pokedexService = pokedexService;
        this.pokemonUtils = pokemonUtils;
        this.enemyTeamFactory = enemyTeamFactory;
    }

    public BattleContext initBattle(String trainerId){
        Pokedex pokedex = pokedexService.getPokedexByOwner(trainerId);

        if (pokedex.getPokemonTeam().size() < 6){
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "You don't have enough pokemons in team"
            );
        }

        List<BattlePokemon> playerTeam = pokemonUtils.toBattleTeam(pokedex.getPokemonTeamExpanded());

        List<BattlePokemon> enemyTeam = enemyTeamFactory.createRandomTeam();

        return new BattleContext(playerTeam.get(0),enemyTeam.get(0), playerTeam, enemyTeam, trainerId);
    }
}
