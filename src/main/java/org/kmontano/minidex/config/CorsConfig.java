package org.kmontano.minidex.config;

import org.springframework.context.annotation.Bean;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;


/**
 * Clase de configuración principal para las CORS.
 */
public class CorsConfig {
    /**
     * Configura la política CORS para permitir solicitudes desde el frontend.
     *
     * @return configuración de CORS registrada en la aplicación
     */
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration config = new CorsConfiguration();
        config.setAllowedOrigins(List.of("http://localhost:4321")); // dominio del frontend permitido
        config.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS", "PATCH"));
        config.setAllowedHeaders(List.of("*"));  // permite todos los encabezados
        config.setAllowCredentials(true); // permite cookies/autenticación
        config.setMaxAge(3600L);  // cachea preflight requests por 1 hora

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);
        return source;
    }
}
