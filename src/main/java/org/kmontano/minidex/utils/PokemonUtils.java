package org.kmontano.minidex.utils;

import org.kmontano.minidex.domain.pokemon.Pokemon;
import org.kmontano.minidex.domain.pokemon.Stats;
import org.kmontano.minidex.dto.shared.BattlePokemon;
import org.kmontano.minidex.infrastructure.mapper.PokemonResponse;
import org.kmontano.minidex.infrastructure.mapper.StatSlot;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
public class PokemonUtils {
    public  List<String> getRandomMoves(PokemonResponse data, int amount){
        List<String> allMoves = new ArrayList<>(data.getMoves()
                .stream()
                .map(m -> m.getMove().getName())
                .toList()
        );

        Collections.shuffle(allMoves);

        return allMoves.stream().limit(amount).toList();
    }

    public Stats mapStats(PokemonResponse data){
        Map<String, Integer> statsMap = data.getStats()
                .stream()
                .collect(Collectors.toMap(
                        s -> s.getStat().getName(),
                        StatSlot::getBaseStat
                ));

        Stats stats = new Stats();
        stats.setHp(statsMap.getOrDefault("hp", 0))
                .setAttack(statsMap.getOrDefault("attack", 0))
                .setDefense(statsMap.getOrDefault("defense", 0))
                .setSpeed(statsMap.getOrDefault("speed", 0));

        return stats;
    }

    public boolean isShiny(double shinyChance){
        return Math.random() < shinyChance;
    }

    public String selectImage(PokemonResponse data, boolean isShiny) {
        var home = data.getSprites()
                .getOther()
                .getHome();

        if (home == null) return null;

        return isShiny
                ? home.getFrontShiny()
                : home.getFrontDefault();
    }

    public List<BattlePokemon> toBattleTeam(List<Pokemon> team){
        return team.stream().map(BattlePokemon::new).toList();
    }
}
