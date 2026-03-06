package org.kmontano.minidex.domain.battle.engine;

import org.kmontano.minidex.application.service.PokedexService;
import org.kmontano.minidex.application.service.RewardService;
import org.kmontano.minidex.application.service.TrainerService;
import org.kmontano.minidex.domain.battle.model.BattleContext;
import org.kmontano.minidex.domain.battle.model.BattleStatus;
import org.kmontano.minidex.domain.pokedex.Pokedex;
import org.kmontano.minidex.domain.trainer.Trainer;
import org.kmontano.minidex.domain.battle.event.FinishBattleEventDTO;
import org.kmontano.minidex.dto.shared.BattleReward;
import org.springframework.stereotype.Component;

@Component
public class BattleFinisher {
    private final RewardService rewardService;
    private final TrainerService trainerService;
    private final PokedexService pokedexService;

    public BattleFinisher(RewardService rewardService, TrainerService trainerService, PokedexService pokedexService) {
        this.rewardService = rewardService;
        this.trainerService = trainerService;
        this.pokedexService = pokedexService;
    }

    public void finish(BattleContext context, Trainer trainer, BattleEventCollector collector){
        int prevLevel = trainer.getLevel();
        int prevXp = trainer.getXp();

        BattleReward reward = rewardService.calculateRewardByBattle(context);

        rewardService.applyBattleReward(trainer, reward);

        if (context.getStatus() == BattleStatus.PLAYER_WON){
            Pokedex pokedex = pokedexService.getPokedexByOwner(trainer.getId());

            pokedex.upLevelTeamByWin();
            pokedexService.update(pokedex);

            trainer.onWin();
        } else {
            trainer.onLose();
        }

        trainerService.update(trainer);

        FinishBattleEventDTO event = new FinishBattleEventDTO();
        event.setPlayerWin(context.getStatus() == BattleStatus.PLAYER_WON);
        event.setLevelUp(prevLevel != trainer.getLevel());
        event.setPreviousXp(prevXp);
        event.setLevel(trainer.getLevel());
        event.setCoins(reward.getCoins());
        event.setXpEarned(reward.getExperience());
        event.setNewXp(trainer.getXp());

        collector.add(event);
    }
}
