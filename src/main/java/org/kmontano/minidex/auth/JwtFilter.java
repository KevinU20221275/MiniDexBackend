package org.kmontano.minidex.auth;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.kmontano.minidex.models.Trainer;
import org.kmontano.minidex.services.TrainerService;
import org.kmontano.minidex.utils.JwtUtil;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

/**
 * Filtro que intercepta todas las solicitudes HTTP para validar el token JWT.
 *
 * - Extrae el token JWT del encabezado "Authorization".
 * - Valida y obtiene el nombre de usuario desde el token.
 * - Recupera el usuario (Trainer) correspondiente.
 * - Establece el usuario autenticado en el contexto de seguridad de Spring.
 */
@Component
public class JwtFilter extends OncePerRequestFilter {
    private final JwtUtil jwtUtil;
    private final TrainerService trainerService;

    // Inyección de dependencias
    public JwtFilter(JwtUtil jwtUtil, TrainerService trainerService) {
        this.jwtUtil = jwtUtil;
        this.trainerService = trainerService;
    }

    /**
     * Método que se ejecuta en cada solicitud HTTP para comprobar la validez del token JWT.
     */
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        try {
            // extrae el token
            String token = jwtUtil.extractToken(request);

            if (token != null) {
                String username = jwtUtil.extractUsername(token);

                if (username != null && jwtUtil.validateToken(token, username)) {
                    Optional<Trainer> trainerOptional = trainerService.findTrainerByUsername(username);

                    if (trainerOptional.isPresent()) {
                        Trainer trainer = trainerOptional.get();
                        // Crea el objeto de autenticación para Spring Security
                        UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(
                                trainer, null, List.of()
                        );

                        // Asigna la autenticación al contexto actual
                        SecurityContextHolder.getContext().setAuthentication(auth);
                    }
                }
            }
        } catch (Exception e) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            return; // corta el flujo si hay error grave
        }

        // Continua con el siguiente filtro en la cadena
        filterChain.doFilter(request, response);
    }
}
