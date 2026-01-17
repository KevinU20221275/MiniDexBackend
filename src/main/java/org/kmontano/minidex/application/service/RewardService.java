package org.kmontano.minidex.application.service;

import org.kmontano.minidex.domain.battle.model.BattleContext;
import org.kmontano.minidex.domain.trainer.Trainer;
import org.kmontano.minidex.dto.shared.BattleReward;

public interface BattleRewardService {
    BattleReward calculate(BattleContext context);
    void applyReward(Trainer trainer, BattleReward reward);
}
