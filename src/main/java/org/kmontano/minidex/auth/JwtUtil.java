package org.kmontano.minidex.auth;

import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;

/**
 * Utilidad para manejar tokens JWT:
 * - Generar tokens.
 * - Extraer información (claims, username).
 * - Validar tokens.
 * - Extraer el token del encabezado HTTP.
 */
@Component
public class JwtUtil {
    private static final String SECRET_STRING = "UnaClaveSuperSecretaDe256bitsParaHS256!!1234567890";
    private static final Key SECRET_KEY = Keys.hmacShaKeyFor(SECRET_STRING.getBytes());

    /**
     * Genera un token JWT con el nombre de usuario y duración de 1 día.
     */
    public String generateToken(String username){
        return Jwts.builder()
                .setSubject(username)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + 86400000)) // 1 dia
                .signWith(SignatureAlgorithm.HS256, SECRET_KEY)
                .compact();
    }

    /**
     * Extrae el nombre de usuario (subject) del token JWT.
     */
    public String extractUsername(String token){
        return Jwts.parserBuilder()
                .setSigningKey(SECRET_KEY)
                .build()
                .parseClaimsJws(token)
                .getBody()
                .getSubject();
    }

    /**
     * Valida el token verificando firma y expiración.
     */
    public boolean validateToken(String token, String username) {
        try {
            String extractedUsername = extractUsername(token);
            return extractedUsername.equals(username) && !isTokenExpired(token);
        } catch (JwtException | IllegalArgumentException e) {
            return false; // token inválido o mal formado
        }
    }

    /**
     * Verifica si el token ya expiró.
     */
    private boolean isTokenExpired(String token) {
        Date expiration = Jwts.parserBuilder()
                .setSigningKey(SECRET_KEY)
                .build()
                .parseClaimsJws(token)
                .getBody()
                .getExpiration();
        return expiration.before(new Date());
    }

    /**
     * Extrae el token JWT del encabezado "Authorization" de una solicitud HTTP.
     *
     * @param request solicitud HTTP
     * @return el token sin el prefijo "Bearer ", o null si no existe
     */
    public String extractToken(HttpServletRequest request) {
        String header = request.getHeader("Authorization");
        if (header != null && header.startsWith("Bearer ")) {
            return header.substring(7);
        }
        return null;
    }
}
