package org.kmontano.minidex.application.facade;

import org.kmontano.minidex.domain.pokedex.Pokedex;
import org.kmontano.minidex.domain.enemy.service.EnemyAiDecisionService;
import org.kmontano.minidex.domain.enemy.EnemyBattleState;
import org.kmontano.minidex.domain.battle.*;
import org.kmontano.minidex.domain.battle.action.AttackAction;
import org.kmontano.minidex.domain.battle.action.SwitchAction;
import org.kmontano.minidex.domain.battle.model.BattleContext;
import org.kmontano.minidex.domain.battle.model.BattleStatus;
import org.kmontano.minidex.domain.battle.model.BattleTurn;
import org.kmontano.minidex.domain.pokemon.Move;
import org.kmontano.minidex.domain.pokemon.Pokemon;
import org.kmontano.minidex.domain.trainer.Trainer;
import org.kmontano.minidex.dto.shared.BattlePokemon;
import org.kmontano.minidex.dto.shared.BattleReward;
import org.kmontano.minidex.dto.request.BattleTurnRequest;
import org.kmontano.minidex.factory.PokemonFactory;
import org.kmontano.minidex.infrastructure.mapper.PokemonResponse;
import org.kmontano.minidex.application.serviceImpl.BattleRewardServiceImpl;
import org.kmontano.minidex.application.service.PokedexService;
import org.kmontano.minidex.infrastructure.api.PokemonApiClient;
import org.kmontano.minidex.utils.PokemonUtils;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ThreadLocalRandom;

@Component
public class BattleFacade {
    private PokemonApiClient pokemonApiClient;
    private PokemonUtils pokemonUtils;
    private PokemonFactory pokemonFactory;
    private EnemyAiDecisionService enemyAiDecisionService;
    private BattleRewardServiceImpl battleRewardService;
    private PokedexService pokedexService;

    public BattleFacade(PokemonApiClient pokemonApiClient, PokemonUtils pokemonUtils, PokemonFactory pokemonFactory, EnemyAiDecisionService enemyAiDecisionService, BattleRewardServiceImpl battleRewardService, PokedexService pokedexService) {
        this.pokemonApiClient = pokemonApiClient;
        this.pokemonUtils = pokemonUtils;
        this.pokemonFactory = pokemonFactory;
        this.enemyAiDecisionService = enemyAiDecisionService;
        this.battleRewardService = battleRewardService;
        this.pokedexService = pokedexService;
    }

    public BattleContext initBattle(String trainerId){
        Pokedex pokedex = pokedexService.getPokedexByOwner(trainerId).orElseThrow(() ->
                new ResponseStatusException(HttpStatus.NOT_FOUND, "No se encontro la pokedex del entrenador"));

        if (pokedex.getPokemonTeam().size() < 6) throw  new ResponseStatusException(HttpStatus.BAD_REQUEST, "No tienes sufientes pokemons en el team");

        List<BattlePokemon> enemyTeam = loadAiTeam();
        List<BattlePokemon> playerTeamToBattle = pokemonUtils.toBattleTeam(pokedex.getPokemonTeamExpanded());
        BattleContext battleContext = new BattleContext(playerTeamToBattle.get(0), enemyTeam.get(0), BattleTurn.PLAYER, BattleStatus.IN_PROGRESS);
        battleContext.setEnemyTeam(enemyTeam);
        battleContext.setPlayerTeam(playerTeamToBattle);

        return battleContext;
    }

    public List<BattlePokemon> loadAiTeam(){
        List<Pokemon> pokemons = new ArrayList<>();
        for (int i = 0; i < 6; i++){
            int randonId = ThreadLocalRandom.current().nextInt(1, 251);
            PokemonResponse pr = pokemonApiClient.getPokemonById(randonId);
            Pokemon p = pokemonFactory.toFullPokemon(pr, true);
            pokemons.add(p);
        }

        return pokemonUtils.toBattleTeam(pokemons);
    }

    public BattleAction resolvePlayerAction(BattleContext context, BattleTurnRequest request){
        return switch (request.getAction()){
            case ATTACK -> {
                Move move = context.getPlayer().getMoves().stream()
                        .filter(m -> m.getMoveName().equalsIgnoreCase(request.getMoveName()))
                        .findFirst()
                        .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Movimiento invalido"));
                yield new AttackAction(move);
            }
            case SWITCH -> {
                BattlePokemon target = context.getPlayerTeam().stream()
                        .filter(p -> p.getPokemonId().equals(request.getPokemonUuid()))
                        .filter(p -> !p.isFainted())
                        .findFirst()
                        .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Cambio invalido"));

                yield new SwitchAction(target);
            }
        };
    }

    public BattleAction resolveEnemyAction(BattleContext context){
        BattlePokemon enemy = context.getEnemy();
        BattlePokemon player  = context.getPlayer();
        EnemyBattleState state = context.getEnemyBattleState();

        return enemyAiDecisionService.decide(enemy, player, context.getEnemyTeam(), state);
    }

    public boolean lostBattle(List<BattlePokemon> team){
        return team.stream().allMatch(BattlePokemon::isFainted);
    }

    public void handleEnemyFaint(BattleContext context){
        Optional<BattlePokemon> pokemon = enemyAiDecisionService.swicthByKO(context.getEnemy(), context.getPlayer(), context.getEnemyTeam());
        if (pokemon.isPresent()){
            context.setEnemy(pokemon.get());
        } else {
            context.setStatus(BattleStatus.PLAYER_WON);
        }
    }

    public void applyReward(BattleContext context, Trainer trainer){
        BattleReward reward = battleRewardService.calculate(context);
        battleRewardService.applyReward(trainer, reward);
    }

    public void finishBattleIfWon(BattleContext context, Trainer trainer){
        applyReward(context, trainer);
        pokedexService.getPokedexByOwner(trainer.getId())
                .ifPresent(pokedex -> {
                    pokedex.upLevelTeamByWin();
                    pokedexService.update(pokedex);
                });
    }
}
