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

/**
 * Aggregate root representing a Trainer's Pokedex.
 *
 * The Pokedex is responsible for managing:
 *
 *  - The ownership of the pokedex
 *  - The list of captured Pokémon
 *  - The composition and rules of the active Pokémon team
 *  - Team progression after battles
 *
 * This class enforces all domain rules related to Pokémon team management,
 * such as team size limits and validation of Pokémon ownership.
 */
@Document(collection = "pokedex")
public class Pokedex {
    @Id
    private String id;

    /**
     * Identifier of the trainer who owns this Pokedex.
     */
    private String ownerId;

    /**
     * List of Pokémon UUIDs that form the active team.
     * The team is limited to a maximum of 6 Pokémon.
     */
    private List<String> pokemonTeam = new ArrayList<>();

    /**
     * List of all Pokémon owned by the trainer.
     */
    private List<Pokemon> pokemons = new ArrayList<>();

    /**
     * Protected constructor required by the persistence framework.
     */
    protected Pokedex() {
        // For persistence
    }

    /**
     * Creates a new Pokedex for a trainer.
     *
     * @param ownerId the trainer identifier
     * @throws DomainValidationException if the ownerId is null or blank
     */
    public Pokedex(String ownerId){
        if (ownerId == null || ownerId.isBlank()) {
            throw new DomainValidationException("Owner id is required");
        }
        this.ownerId = ownerId;
    }

    // getters
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

    /* Domain logic */

    /**
     * Adds a Pokémon to the Pokedex.
     *
     * @param pokemon the Pokémon to add
     */
    public void addPokemon(Pokemon pokemon) {
        pokemons.add(pokemon);
    }

    /**
     * Adds a Pokémon to the active team.
     *
     * Domain rules:
     *
     *  - The team cannot exceed 6 Pokémon
     *  - The Pokémon must exist in the Pokedex
     *  - The Pokémon cannot already be in the team
     *
     * @param pokemonId the Pokémon UUID
     * @throws DomainConflictException if the team is full or the Pokémon is already in the team
     * @throws DomainValidationException if the Pokémon does not exist in the Pokedex
     */
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

    /**
     * Removes a Pokémon from the active team.
     *
     * @param pokemonId the Pokémon UUID
     * @throws DomainConflictException if the Pokémon is not part of the team
     */
    public void removePokemonFromTeam(String pokemonId){
        if (!pokemonTeam.remove(pokemonId)){
            throw new DomainConflictException("Pokemon is not in the team");
        }
    }

    /**
     * Returns the active team expanded into full Pokémon objects.
     *
     * @return list of Pokémon in the active team
     */
    public List<Pokemon> getPokemonTeamExpanded(){
        if (pokemonTeam.isEmpty()) return List.of();

        Map<String, Pokemon> pokemonMap = pokemons.stream()
                .collect(Collectors.toMap(Pokemon::getUuid, p -> p));

        return pokemonTeam.stream()
                .map(pokemonMap::get)
                .filter(Objects::nonNull)
                .toList();
    }

    /**
     * Increases the level of all Pokémon in the active team after a battle win.
     */
    public void upLevelTeamByWin(){
        Map<String, Pokemon> pokemonMap = pokemons.stream()
                .collect(Collectors.toMap(Pokemon::getUuid, p -> p));

        pokemonTeam.stream().map(pokemonMap::get)
                .filter(Objects::nonNull)
                .forEach(p -> p.setLevel(p.getLevel() + 1));
    }
}
