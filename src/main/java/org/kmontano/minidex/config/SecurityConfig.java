package org.kmontano.minidex.config;

import org.kmontano.minidex.auth.JwtFilter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

/**
 * Clase de configuración principal para la seguridad de la aplicación.
 *
 * - Habilita la seguridad web con Spring Security.
 * - Configura CORS, CSRF, sesiones sin estado y autorización de rutas.
 * - Agrega un filtro personalizado JWT para validar tokens en cada solicitud.
 */
@Configuration
@EnableWebSecurity
public class SecurityConfig {
    // Filtro JWT personalizado para manejar autenticación basada en token
    private final JwtFilter jwtFilter;

    @Value("${api.version}")
    private String apiVersion;

    // Inyección del filtro JWT mediante constructor
    public SecurityConfig(JwtFilter jwtFilter) {
        this.jwtFilter = jwtFilter;
    }

    /**
     * Define la cadena de filtros de seguridad de Spring Security.
     *
     * @param http objeto HttpSecurity para configurar las reglas
     * @return el SecurityFilterChain configurado
     * @throws Exception si ocurre un error durante la configuración
     */
    @Bean
    SecurityFilterChain configureSecurity(HttpSecurity http) throws Exception {
        // constante para agrupar las rutas publicas
        String[] PUBLIC_ENDPOINTS = {
                "/api/" + apiVersion + "/auth/**",
                "/v3/api-docs/**"
        };

        return http
                // Configura CORS usando el bean corsConfigurationSource() de la clase CorsConfig
                .cors(Customizer.withDefaults())
                // Desactiva CSRF (recomendado en APIs REST sin sesiones)
                .csrf(csrf -> csrf.disable())
                // Desactiva autenticación básica y formulario (usamos JWT)
                .httpBasic(httpBasic -> httpBasic.disable())
                .formLogin(form -> form.disable())
                // Configura las rutas y permisos
                .authorizeHttpRequests(
                        auth -> auth
                                .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()
                                .requestMatchers(PUBLIC_ENDPOINTS).permitAll()
                                .anyRequest().authenticated()
                )
                // Permite mostrar la consola H2 (solo para desarrollo)
                .headers(headers -> headers.frameOptions(frame -> frame.disable()))
                // Define que la sesión sea stateless (sin almacenar estado del usuario)
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                // Agrega el filtro JWT antes del filtro de autenticación por usuario/contraseña
                .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class)
                .build();
    }
}
