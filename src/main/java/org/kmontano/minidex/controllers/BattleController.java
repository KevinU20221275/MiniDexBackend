package org.kmontano.minidex.controllers;

import org.kmontano.minidex.domain.battle.*;
import org.kmontano.minidex.domain.battle.engine.BattleEngine;
import org.kmontano.minidex.domain.battle.model.BattleContext;
import org.kmontano.minidex.domain.battle.model.BattleStatus;
import org.kmontano.minidex.domain.trainer.Trainer;
import org.kmontano.minidex.application.facade.BattleFacade;
import org.kmontano.minidex.application.service.BattleSessionService;
import org.kmontano.minidex.application.service.TrainerService;
import org.kmontano.minidex.auth.AuthUtils;
import org.kmontano.minidex.dto.request.BattleTurnRequest;
import org.kmontano.minidex.dto.request.StartBattleRequest;
import org.kmontano.minidex.dto.response.BattleTurnResponse;
import org.kmontano.minidex.dto.response.StartBattleResponse;
import org.kmontano.minidex.dto.shared.ActionType;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping("/battle")
public class BattleController {
    private final BattleSessionService battleSessionService;
    private final BattleEngine engine;
    private final TrainerService trainerService;
    private final BattleFacade battleFacade;

    public BattleController(BattleSessionService battleSessionService, BattleEngine engine, TrainerService trainerService, BattleFacade battleFacade) {
        this.battleSessionService = battleSessionService;
        this.engine = engine;
        this.trainerService = trainerService;
        this.battleFacade = battleFacade;
    }

    @PostMapping("/start")
    public ResponseEntity<StartBattleResponse> startBattle(Authentication authentication, StartBattleRequest request){
        Trainer trainer = AuthUtils.getAuthenticatedTrainer(authentication);

        String enemyName = request.getEnemyName();

        BattleContext battleContext = battleFacade.initBattle(trainer.getId());

        String battleId = battleSessionService.createBattle(battleContext);

        return ResponseEntity.ok(new StartBattleResponse(battleId, battleContext, enemyName));
    }

    @PostMapping("/turn")
    public ResponseEntity<BattleTurnResponse> playTurn(@RequestBody BattleTurnRequest request, Authentication authentication){
        Trainer trainer = AuthUtils.getAuthenticatedTrainer(authentication);

        BattleContext context = battleSessionService.getBattle(request.getBattleId());

        if (context.getStatus() != BattleStatus.IN_PROGRESS){
            return ResponseEntity.badRequest().build();
        }

        if (request.getAction() == ActionType.ATTACK && context.getPlayer().isFainted()){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Tu pokemon esta debilitado, debes cambiar");
        }

        BattleAction playerAction = battleFacade.resolvePlayerAction(context, request);
        engine.executeTurn(context, playerAction);

        // KO enemigo
        if (context.getEnemy().isFainted()){
            battleFacade.handleEnemyFaint(context);
            if (battleFacade.lostBattle(context.getEnemyTeam())){
                context.setStatus(BattleStatus.PLAYER_WON);
                battleFacade.finishBattleIfWon(context, trainer);
                trainerService.update(trainer);
                return ResponseEntity.ok(new BattleTurnResponse(context));
            }
        }

        //
        BattleAction enemyAction = battleFacade.resolveEnemyAction(context);
        engine.executeTurn(context, enemyAction);

        if (context.getPlayer().isFainted()){
            if (battleFacade.lostBattle(context.getPlayerTeam())){
                context.setStatus(BattleStatus.ENEMY_WON);
            }
        }

        return ResponseEntity.ok(new BattleTurnResponse(context));
    }
}
