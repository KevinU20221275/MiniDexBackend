package org.kmontano.minidex.domain.pokedex;

import org.kmontano.minidex.domain.pokemon.Pokemon;
import org.kmontano.minidex.exception.DomainConflictException;
import org.kmontano.minidex.exception.DomainValidationException;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

@Document(collection = "pokedex")
public class Pokedex {
    @Id
    private String id;
    private String ownerId; // trainer id
    private List<String> pokemonTeam = new ArrayList<>();
    private List<Pokemon> pokemons = new ArrayList<>();

    protected Pokedex() {
        // For persistence
    }

    public Pokedex(String ownerId){
        if (ownerId == null || ownerId.isBlank()) {
            throw new DomainValidationException("Owner id is required");
        }
        this.ownerId = ownerId;
    }

    public String getId() {
        return id;
    }

    public String getOwnerId() {
        return ownerId;
    }

    public List<String> getPokemonTeam() {
        return List.copyOf(pokemonTeam);
    }

    public List<Pokemon> getPokemons() {
        return List.copyOf(pokemons);
    }

    public void addPokemon(Pokemon pokemon) {
        pokemons.add(pokemon);
    }

    public void addPokemonToTeam(String pokemonId){
        if (pokemonTeam.size() >= 6) {
            throw new DomainConflictException("Team is already full");
        }

        boolean exists = pokemons.stream()
                .anyMatch(p -> p.getUuid().equals(pokemonId));

        if (!exists) {
            throw new DomainValidationException("Pokemon is not in the Pokedex");
        }

        if (pokemonTeam.contains(pokemonId)) {
            throw new DomainConflictException("Pokemon is already in the team");
        }

        pokemonTeam.add(pokemonId);
    }

    public void removePokemonFromTeam(String pokemonId){
        if (!pokemonTeam.remove(pokemonId)){
            throw new DomainConflictException("Pokemon is not in the team");
        }
    }

    public List<Pokemon> getPokemonTeamExpanded(){
        if (pokemonTeam.isEmpty()) return List.of();

        Map<String, Pokemon> pokemonMap = pokemons.stream()
                .collect(Collectors.toMap(Pokemon::getUuid, p -> p));

        return pokemonTeam.stream()
                .map(pokemonMap::get)
                .filter(Objects::nonNull)
                .toList();
    }

    public void upLevelTeamByWin(){
        Map<String, Pokemon> pokemonMap = pokemons.stream()
                .collect(Collectors.toMap(Pokemon::getUuid, p -> p));

        pokemonTeam.stream().map(pokemonMap::get)
                .filter(Objects::nonNull)
                .forEach(p -> p.setLevel(p.getLevel() + 1));
    }
}
