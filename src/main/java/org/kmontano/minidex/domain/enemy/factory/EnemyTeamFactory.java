package org.kmontano.minidex.domain.enemy.factory;

import org.kmontano.minidex.domain.pokemon.Pokemon;
import org.kmontano.minidex.dto.shared.BattlePokemon;
import org.kmontano.minidex.factory.PokemonFactory;
import org.kmontano.minidex.infrastructure.api.PokemonApiClient;
import org.kmontano.minidex.infrastructure.mapper.PokemonResponse;
import org.kmontano.minidex.utils.PokemonUtils;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

@Component
public class EnemyTeamFactory {
    private final PokemonFactory pokemonFactory;
    private final PokemonApiClient pokemonApiClient;
    private final PokemonUtils pokemonUtils;

    public EnemyTeamFactory(PokemonFactory pokemonFactory, PokemonApiClient pokemonApiClient, PokemonUtils pokemonUtils) {
        this.pokemonFactory = pokemonFactory;
        this.pokemonApiClient = pokemonApiClient;
        this.pokemonUtils = pokemonUtils;
    }

    public List<BattlePokemon> createRandomTeam(){
        List<Pokemon> pokemons = new ArrayList<>();

        for (int i = 0; i < 6; i++){
            int ramdomId = ThreadLocalRandom.current().nextInt(1, 251);
            PokemonResponse pr = pokemonApiClient.getPokemonById(ramdomId);
            Pokemon p = pokemonFactory.toFullPokemon(pr, true);
            // generate random level between 1 - 30 to make enemy pokemon strongest
            int randomLevel = ThreadLocalRandom.current().nextInt(1, 31);
            p.setLevel(randomLevel);
            pokemons.add(p);
        }

        return pokemonUtils.toBattleTeam(pokemons);
    }
}
