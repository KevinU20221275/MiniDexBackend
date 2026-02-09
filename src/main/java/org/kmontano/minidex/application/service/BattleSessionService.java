package org.kmontano.minidex.application.service;

import org.kmontano.minidex.domain.battle.model.BattleContext;

public interface BattleSessionService {
    String createBattle(BattleContext context);
    BattleContext getBattle(String battleId);
    void removeBattle(String battleId);
}
