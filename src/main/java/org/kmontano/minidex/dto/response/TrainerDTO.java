package org.kmontano.minidex.dto.response;

import lombok.Data;
import org.kmontano.minidex.domain.trainer.DailyPackStatus;
import org.kmontano.minidex.domain.trainer.Trainer;

/**
 * Data Transfer Object (DTO) para exponer información del entrenador
 * sin incluir datos sensibles como la contraseña.
 */
@Data
public class TrainerDTO {
    private String id;
    private String name;
    private String username;
    private Integer level;
    private Integer xp;
    private Integer coins;
    private Integer wins;
    private Integer loses;
    private DailyPackStatus dailyPackStatus;

    public TrainerDTO(Trainer t){
        this.id = t.getId();
        this.name = t.getName();
        this.username = t.getUsername();
        this.level = t.getLevel();
        this.xp = t.getXp();
        this.coins = t.getCoins();
        this.wins = t.getWins();
        this.loses = t.getLoses();
        this.dailyPackStatus = t.getDailyPack();
    }
}
