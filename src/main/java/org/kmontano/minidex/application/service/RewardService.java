package org.kmontano.minidex.application.service;

import org.kmontano.minidex.domain.battle.model.BattleContext;
import org.kmontano.minidex.domain.pokemon.Pokemon;
import org.kmontano.minidex.domain.trainer.Trainer;
import org.kmontano.minidex.dto.shared.BattleReward;

public interface RewardService {
    BattleReward calculateRewardByBattle(BattleContext context);
    void applyBattleReward(Trainer trainer, BattleReward reward);
    int calculateRewardByTransferPokemon(Pokemon pokemon);
}
