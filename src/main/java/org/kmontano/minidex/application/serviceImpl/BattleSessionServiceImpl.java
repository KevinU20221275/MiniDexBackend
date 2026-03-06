package org.kmontano.minidex.application.serviceImpl;

import org.kmontano.minidex.application.service.BattleSessionService;
import org.kmontano.minidex.domain.battle.model.BattleContext;
import org.kmontano.minidex.domain.battle.engine.TimedBattle;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class BattleSessionServiceImpl implements BattleSessionService {
    private final Map<String, TimedBattle> battles = new ConcurrentHashMap<>();

    @Override
    public String createBattle(BattleContext context){
        String battleId = UUID.randomUUID().toString();
        long createdAt = System.currentTimeMillis();
        TimedBattle timedBattle = new TimedBattle(context, createdAt);
        battles.put(battleId, timedBattle);
        return battleId;
    }

    @Override
    public BattleContext getBattle(String battleId,String trainerId){
        TimedBattle timedBattle = battles.get(battleId);
        if (timedBattle == null){
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND, "Battle not found"
            );
        }

        if (timedBattle.isExpired()){
            battles.remove(battleId);
            throw new ResponseStatusException(HttpStatus.GONE, "Battle expired");
        }

        if (!timedBattle.getContext().getTrainerId().equals(trainerId)){
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Not your battle");
        }

        return timedBattle.getContext();
    }

    @Override
    public void removeBattle(String battleId){
        battles.remove(battleId);
    }
}
