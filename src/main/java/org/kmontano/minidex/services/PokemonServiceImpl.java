package org.kmontano.minidex.services;

import org.kmontano.minidex.dto.PokemonDTO;
import org.kmontano.minidex.models.Pokemon;
import org.kmontano.minidex.models.PokemonType;
import org.kmontano.minidex.repositories.PokemonRepository;
import org.kmontano.minidex.repositories.TypeRepository;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class PokemonServiceImpl implements PokemonService {
    private final PokemonRepository repository;
    private final TypeRepository typeRepository;

    public PokemonServiceImpl(PokemonRepository repository, TypeRepository typeRepository) {
        this.repository = repository;
        this.typeRepository = typeRepository;
    }

    @Override
    public List<PokemonDTO> findAll() {
        return repository.findAll().stream()
                .map(PokemonDTO::new)
                .collect(Collectors.toList());
    }

    /**
     * Guarda un Pokémon, evitando duplicados.
     * Si el Pokémon ya existe en DB, retorna la instancia existente.
     * Mapea tipos existentes o los crea si no existen.
     */
    @Override
    public Pokemon save(Pokemon pokemon) {
        // Verificar si el Pokémon ya existe por nombre
        Optional<Pokemon> existingPokemon = repository.findByName(pokemon.getName());
        if (existingPokemon.isPresent()) {
            return existingPokemon.get();
        }

        // Mapear tipos
        List<String> typeNames = pokemon.getTypes().stream()
                .map(PokemonType::getName)
                .collect(Collectors.toList());

        List<PokemonType> mappedTypes = new ArrayList<>();

        for (String typeName: typeNames){
            PokemonType type = typeRepository.findByName(typeName)
                    .orElseGet(() -> typeRepository.save(new PokemonType(typeName)));
            mappedTypes.add(type);
        }

        pokemon.setTypes(mappedTypes);

        return repository.save(pokemon);
    }

    @Override
    public Optional<Pokemon> findById(Long id) {
        Optional<Pokemon> pokemon = repository.findById(id);
        return pokemon;
    }

    @Override
    public Optional<Pokemon> findByName(String name) {
        return repository.findByName(name);
    }

    @Override
    public List<Pokemon> findAllByIdWithTypes(List<Long> pokemonsIds) {
        return repository.findAllByIdWithTypes(pokemonsIds);
    }
}
