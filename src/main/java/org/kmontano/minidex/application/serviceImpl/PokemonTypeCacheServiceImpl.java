package org.kmontano.minidex.application.serviceImpl;

import org.kmontano.minidex.application.service.PokemonTypeCacheService;
import org.kmontano.minidex.domain.pokemon.PokemonTypeCache;
import org.kmontano.minidex.infrastructure.api.dto.TypeApiResponse;
import org.kmontano.minidex.infrastructure.repository.PokemonTypeCacheRepository;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.time.Instant;

@Service
public class PokemonTypeCacheServiceImpl implements PokemonTypeCacheService {
    private final PokemonTypeCacheRepository repository;
    private final RestTemplate rest = new RestTemplate();

    public PokemonTypeCacheServiceImpl(PokemonTypeCacheRepository repository) {
        this.repository = repository;
    }

    @Override
    public PokemonTypeCache getType(String typeUrl) {

        int id = extractId(typeUrl);

        // Buscar en cache (Mongo)
        return repository.findById(id)
                .orElseGet(() -> fetchAndCache(typeUrl, id));
    }

    private PokemonTypeCache fetchAndCache(String url, int id){
        TypeApiResponse api = rest.getForObject(url, TypeApiResponse.class);

        assert api != null;

        String icon = api.getSprites().getGenerationIx().getScarletViolet().getNameIcon();

        PokemonTypeCache cache = new PokemonTypeCache();
        cache.setId(id);
        cache.setName(api.getName());
        cache.setIconUrl(icon);
        cache.setCacheAt(Instant.now());

        return repository.save(cache);
    }

    private int extractId(String url) {
        String[] parts = url.split("/");
        return Integer.parseInt(parts[parts.length - 1]);
    }
}
