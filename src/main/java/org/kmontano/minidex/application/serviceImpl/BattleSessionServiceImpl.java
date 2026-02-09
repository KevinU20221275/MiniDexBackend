package org.kmontano.minidex.application.serviceImpl;

import org.kmontano.minidex.application.service.BattleSessionService;
import org.kmontano.minidex.domain.battle.model.BattleContext;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class BattleSessionServiceImpl implements BattleSessionService {
    private final Map<String, BattleContext> battles = new ConcurrentHashMap<>();

    @Override
    public String createBattle(BattleContext context){
        String battleId = UUID.randomUUID().toString();
        battles.put(battleId, context);
        return battleId;
    }

    @Override
    public BattleContext getBattle(String battleId){
        BattleContext context = battles.get(battleId);
        if (context == null){
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND, "Battle not found"
            );
        }
        return context;
    }

    @Override
    public void removeBattle(String battleId){
        battles.remove(battleId);
    }
}
