package org.kmontano.minidex.application.serviceImpl;

import org.kmontano.minidex.application.service.RewardService;
import org.kmontano.minidex.domain.battle.model.BattleContext;
import org.kmontano.minidex.domain.battle.model.BattleStatus;
import org.kmontano.minidex.domain.pokemon.Pokemon;
import org.kmontano.minidex.domain.trainer.Trainer;
import org.kmontano.minidex.dto.shared.BattleReward;
import org.springframework.stereotype.Service;

@Service
public class RewardServiceImpl implements RewardService {
    private static final int BASE_COINS = 100;
    private static final int BASE_XP = 50;

    @Override
    public BattleReward calculateRewardByBattle(BattleContext context){
        int aliveTeam = (int) context.getPlayerTeam()
                .stream()
                .filter(p -> !p.isFainted())
                .count();

        int coins = BASE_COINS + (aliveTeam * 20);
        int experience = BASE_XP + (aliveTeam * 10);

        int finalCoins = context.getStatus() == BattleStatus.PLAYER_WON ? coins : coins / 2;

        return new BattleReward(finalCoins, experience);
    }

    @Override
    public void applyBattleReward(Trainer trainer, BattleReward reward){
        trainer.addCoins(reward.getCoins());
        trainer.addXp(reward.getExperience());
    }


    public int calculateRewardByTransferPokemon(Pokemon pokemon){
        int base = pokemon.getRarity().getBaseCoins();

        int statsBonus = (pokemon.getStats().getAttack() + pokemon.getStats().getDefense() + pokemon.getStats().getHp()) / 10;

        return base + statsBonus + pokemon.getLevel();
    }
}
