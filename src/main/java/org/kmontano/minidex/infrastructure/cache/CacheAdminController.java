package org.kmontano.minidex.infrastructure.cache;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/${api.version}/admin/cache")
@RequiredArgsConstructor
public class CacheAdminController {

    private final PackPokemonCacheService cacheService;

    @PostMapping("/preload")
    public ResponseEntity<String> preload() {
        cacheService.preloadMissingPokemon();
        return ResponseEntity.ok("Cache loading started");
    }
}

