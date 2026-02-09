package org.kmontano.minidex.auth;

import org.kmontano.minidex.domain.trainer.Trainer;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.web.server.ResponseStatusException;

import java.util.Map;

public class AuthUtils {

    // Metodo helper para obtener el trainer autenticado o lanza 401 si no esta presente
    public static Trainer getAuthenticatedTrainer(Authentication authentication){
        if (authentication == null || !(authentication.getPrincipal() instanceof Trainer)) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, Map.of("message", "Usuario no authenticado").toString());
        }

        return (Trainer) authentication.getPrincipal();
    }
}
