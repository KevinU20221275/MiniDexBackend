package org.kmontano.minidex.application.facade;

import org.kmontano.minidex.application.service.BattleSessionService;
import org.kmontano.minidex.domain.battle.action.AttackAction;
import org.kmontano.minidex.domain.battle.action.BattleAction;
import org.kmontano.minidex.domain.battle.service.AttackResolutionService;
import org.kmontano.minidex.domain.battle.model.BattleSide;
import org.kmontano.minidex.domain.battle.action.SwitchAction;
import org.kmontano.minidex.domain.battle.engine.BattleEngine;
import org.kmontano.minidex.domain.battle.engine.BattleEventCollector;
import org.kmontano.minidex.domain.battle.engine.BattleFinisher;
import org.kmontano.minidex.domain.battle.engine.BattleInitializer;
import org.kmontano.minidex.domain.battle.model.*;
import org.kmontano.minidex.domain.enemy.service.EnemyAiDecisionService;
import org.kmontano.minidex.domain.pokemon.Move;
import org.kmontano.minidex.domain.trainer.Trainer;
import org.kmontano.minidex.dto.request.BattleTurnRequest;
import org.kmontano.minidex.domain.battle.event.BattleEventDTO;

import org.kmontano.minidex.domain.battle.event.FaintEventDTO;
import org.kmontano.minidex.domain.battle.event.SwitchEventDTO;
import org.kmontano.minidex.dto.response.BattleTurnResponse;
import org.kmontano.minidex.dto.shared.ActionType;
import org.kmontano.minidex.dto.shared.BattlePokemon;
import org.kmontano.minidex.exception.DomainConflictException;
import org.kmontano.minidex.exception.ResourceNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Optional;

/**
 * Facade responsible for coordinating battle operations.
 *
 * <p>This class acts as the main entry point for battle logic
 * from the controller layer.</p>
 *
 * Responsibilities:
 * - Resolve player actions
 * - Execute battle turns
 * - Handle forced switches
 * - Coordinate the battle engine
 *
 * This class simplifies interaction with the battle subsystem
 * and prevents the controller from accessing domain logic directly.
 */
@Component
public class BattleFacade {
    private final BattleInitializer initializer;
    private final BattleEngine engine;
    private final BattleFinisher finisher;
    private final EnemyAiDecisionService enemyAi;
    private final BattleSessionService battleSessionService;
    private final AttackResolutionService attackResolutionService;

    public BattleFacade(BattleInitializer initializer, BattleEngine engine, BattleFinisher finisher, EnemyAiDecisionService enemyAi, BattleSessionService battleSessionService, AttackResolutionService attackResolutionService) {
        this.initializer = initializer;
        this.engine = engine;
        this.finisher = finisher;
        this.enemyAi = enemyAi;
        this.battleSessionService = battleSessionService;
        this.attackResolutionService = attackResolutionService;
    }

    public BattleTurnResponse playTurn(BattleTurnRequest request, Trainer trainer){
        BattleContext context = battleSessionService.getBattle(request.getBattleId(), trainer.getId());

        if (context.getStatus() != BattleStatus.IN_PROGRESS){
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "Battle is already finished"
            );
        }

        if (request.getAction() == ActionType.ATTACK && context.getPlayer().isFainted()){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Your Pokémon has fainted, you must switch");
        }

        if (request.getAction() == ActionType.SWITCH && context.getPlayer().isFainted()){
            BattleEventDTO event = forcePlayerSwitch(context, request.getPokemonUuid());

            return new BattleTurnResponse(context, List.of(event));
        }

        BattleAction playerAction = resolvePlayerAction(context, request);

        List<BattleEventDTO> events = executeTurn(context, playerAction, trainer);

        return new BattleTurnResponse(context, events);
    }

    public BattleContext initBattle(String trainerId){
        return initializer.initBattle(trainerId);
    }

    public List<BattleEventDTO> executeTurn(BattleContext context, BattleAction playerAction, Trainer trainer){
        BattleEventCollector collector = new BattleEventCollector();

        BattleAction enemyAction = enemyAi.decide(context);

        engine.executeTurn(context, playerAction, enemyAction, collector);

        resolvePostTurn(context, trainer, collector);

        return collector.consume();
    }

    public BattleTurnResponse surrender(String battleId, Trainer trainer) {
        BattleContext context = battleSessionService.getBattle(battleId, trainer.getId());

        if (context.getStatus() != BattleStatus.IN_PROGRESS) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Battle already finished");
        }

        BattleEventCollector collector = new BattleEventCollector();

        context.endBattle(BattleStatus.ENEMY_WON);

        finisher.finish(context, trainer, collector);

        battleSessionService.removeBattle(context.getBattleId());

        List<BattleEventDTO> events = collector.consume();

        return new BattleTurnResponse(context, events);
    }

    private void resolvePostTurn(BattleContext context, Trainer trainer, BattleEventCollector eventCollector){
        if (context.getEnemy().isFainted()){
            eventCollector.add(new FaintEventDTO(BattleSide.ENEMY, context.getEnemy().getPokemonId()));


            Optional<BattlePokemon> next = enemyAi.switchByKO(context.getEnemy(), context.getPlayer(), context.getEnemyTeam());

            if (next.isPresent()){
                BattlePokemon snapshot = next.get().copy();
                context.switchEnemy(next.get());

                eventCollector.add(new SwitchEventDTO(BattleSide.ENEMY, snapshot));
            } else {
                context.endBattle(BattleStatus.PLAYER_WON);
                finisher.finish(context, trainer, eventCollector);
                battleSessionService.removeBattle(context.getBattleId());
            }
        }

        if (context.getPlayer().isFainted()){
            eventCollector.add(new FaintEventDTO(BattleSide.PLAYER, context.getPlayer().getPokemonId()));

            if (context.isPlayerDefeated()){
                context.endBattle(BattleStatus.ENEMY_WON);
                finisher.finish(context, trainer, eventCollector);
                battleSessionService.removeBattle(context.getBattleId());
            }
        }
    }

    public BattleAction resolvePlayerAction(BattleContext context, BattleTurnRequest request){
        switch (request.getAction()){
            case ATTACK -> {
                Optional<Move> moveOp = context.getPlayer().getMoves().stream().filter(m -> m.getMoveName().equals(request.getMoveName())).findFirst();
                if (moveOp.isPresent()){
                    return new AttackAction(moveOp.get(), BattleSide.PLAYER, attackResolutionService);
                }
                throw new ResourceNotFoundException("Your pokemon don't know this move");
            }
            case SWITCH -> {
                return new SwitchAction(findPokemon(context, request.getPokemonUuid()), BattleSide.PLAYER);
            }
            default -> {
                throw new IllegalStateException("Action type error");
            }
        }
    }

    public BattleEventDTO forcePlayerSwitch(BattleContext context, String pokemonId){
        BattlePokemon next = findPokemon(context, pokemonId);

        BattlePokemon snapshot = next.copy();
        context.switchPlayer(next);

        return new SwitchEventDTO(BattleSide.PLAYER, snapshot);
    }

    private BattlePokemon findPokemon(BattleContext context, String pokemonId){
        Optional<BattlePokemon> next = context.getPlayerTeam().stream().filter(p -> p.getPokemonId().equals(pokemonId)).findFirst();

        if (next.isPresent()){
            if (!next.get().isFainted()){
                return next.get();
            }
            throw new DomainConflictException("Your pokemon already was defeat");
        }

        throw new ResourceNotFoundException("Pokemon is not in the team");
    }
}
