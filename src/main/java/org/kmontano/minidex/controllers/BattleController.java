package org.kmontano.minidex.controllers;

import org.kmontano.minidex.domain.battle.model.BattleContext;
import org.kmontano.minidex.domain.trainer.Trainer;
import org.kmontano.minidex.application.facade.BattleFacade;
import org.kmontano.minidex.application.service.BattleSessionService;
import org.kmontano.minidex.auth.AuthUtils;
import org.kmontano.minidex.dto.request.BattleTurnRequest;
import org.kmontano.minidex.dto.response.*;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/${api.version}/battle")
public class BattleController {
    private final BattleSessionService battleSessionService;
    private final BattleFacade battleFacade;

    public BattleController(BattleSessionService battleSessionService, BattleFacade battleFacade) {
        this.battleSessionService = battleSessionService;
        this.battleFacade = battleFacade;
    }

    @PostMapping("start")
    public ResponseEntity<StartBattleResponse> startBattle(Authentication authentication){
        Trainer trainer = AuthUtils.getAuthenticatedTrainer(authentication);

        BattleContext battleContext = battleFacade.initBattle(trainer.getId());
        String battleId = battleSessionService.createBattle(battleContext);
        battleContext.setBattleId(battleId);

        StartBattleResponse response = new StartBattleResponse(battleId, battleContext);

        return ResponseEntity.ok(response);
    }

    @PostMapping(
            value = "/turn",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<BattleTurnResponse> playTurn(@RequestBody BattleTurnRequest request, Authentication authentication){
        Trainer trainer = AuthUtils.getAuthenticatedTrainer(authentication);

        BattleTurnResponse response = battleFacade.playTurn(request, trainer);

        return ResponseEntity.ok(response);
    }

    @PostMapping("/{battleId}/surrender")
    public ResponseEntity<BattleTurnResponse> surrender(@PathVariable String battleId, Authentication authentication) {
        Trainer trainer = AuthUtils.getAuthenticatedTrainer(authentication);

        BattleTurnResponse response = battleFacade.surrender(battleId, trainer);

        return ResponseEntity.ok(response);
    }


}
