package org.kmontano.minidex.domain.pokedex;

import org.kmontano.minidex.domain.pokemon.Pokemon;
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

    public Pokedex() {
    }

    public String getId() {
        return id;
    }

    public Pokedex setId(String id) {
        this.id = id;
        return this;
    }

    public String getOwnerId() {
        return ownerId;
    }

    public Pokedex setOwnerId(String ownerId) {
        this.ownerId = ownerId;
        return this;
    }

    public List<String> getPokemonTeam() {
        return pokemonTeam;
    }

    public Pokedex setPokemonTeam(List<String> pokemonTeam) {
        this.pokemonTeam = pokemonTeam;
        return this;
    }

    public List<Pokemon> getPokemons() {
        return pokemons;
    }

    public Pokedex setPokemons(List<Pokemon> pokemons) {
        this.pokemons = pokemons;
        return this;
    }

    public void addPokemonToTeam(String pokemonId){
        if (pokemonTeam.size() >= 6) {
            throw new IllegalStateException("Team completo");
        }

        boolean exists = pokemons.stream()
                .anyMatch(p -> p.getUuid().equals(pokemonId));

        if (!exists) {
            throw new IllegalArgumentException("El Pokémon no está en la Pokédex");
        }

        if (pokemonTeam.contains(pokemonId)) {
            throw new IllegalStateException("El Pokémon ya está en el Team");
        }

        pokemonTeam.add(pokemonId);
    }

    public void removePokemonFromTeam(String pokemonId){
        if (!pokemonTeam.remove(pokemonId)){
            throw new IllegalStateException("El pokemon no esta en el team");
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
