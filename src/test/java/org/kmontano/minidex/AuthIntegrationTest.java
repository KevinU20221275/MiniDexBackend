package org.kmontano.minidex;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.*;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.web.client.DefaultResponseErrorHandler;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Pruebas de integración:
 * Se validan los endpoints reales de registro y login usando H2 y un servidor embebido.
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
class AuthIntegrationTest {

    @LocalServerPort
    private int port;

    private String baseUrl;
    private final TestRestTemplate restTemplate = new TestRestTemplate();

    @Autowired
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        baseUrl = "http://localhost:" + port;

        // Evita que RestTemplate lance excepción en errores HTTP
        restTemplate.getRestTemplate().setErrorHandler(new DefaultResponseErrorHandler() {
            @Override
            public boolean hasError(ClientHttpResponse response) throws IOException {
                // Retorna false para no lanzar excepción por 4xx/5xx
                return false;
            }
        });
    }

    @Test
    @DisplayName("Registro exitoso devuelve TrainerDTO y token JWT")
    void testRegisterAndLoginFlow() {
        // Preparación del payload de registro
        Map<String, String> registerPayload = Map.of(
                "name", "Ash Ketchum",
                "username", "ash",
                "password", "pikachu123"
        );

        // Envío del POST a /auth/register usando RestTemplate
        ResponseEntity<String> registerResponse = restTemplate.postForEntity(
                baseUrl + "/auth/register", registerPayload, String.class);

        // Validaciones del registro
        assertEquals(HttpStatus.CREATED, registerResponse.getStatusCode()); // HTTP 201
        assertTrue(registerResponse.getBody().contains("Ash Ketchum")); // El body contiene el nombre registrado

        // Preparación del payload de login
        Map<String, String> loginPayload = Map.of(
                "username", "ash",
                "password", "pikachu123"
        );

        // Envío del POST a /auth/login usando RestTemplate
        ResponseEntity<String> loginResponse = restTemplate.postForEntity(
                baseUrl + "/auth/login", loginPayload, String.class);

        // Validaciones del login
        assertEquals(HttpStatus.OK, loginResponse.getStatusCode()); // HTTP 200
        assertTrue(loginResponse.getBody().contains("token")); // El body contiene el token JWT
    }

    @Test
    void registerExistingUsername_shouldReturn409() throws Exception {
        // Registro previo con el username "ash"
        Map<String, String> request1 = Map.of(
                "name", "Ash",
                "username", "ash",
                "password", "hashed"
        );

        restTemplate.postForEntity(baseUrl + "/auth/register", request1, String.class);

        // Intento duplicado con el mismo username
        Map<String, String> request2 = Map.of(
                "name", "Ash2",
                "username", "ash",
                "password", "pikachu123"
        );

        // Se espera un status 409
        ResponseEntity<String> response = restTemplate.postForEntity(baseUrl + "/auth/register", request2, String.class);

        assertEquals(409, response.getStatusCodeValue());
        assertTrue(response.getBody().contains("El nombre de usuario ya existe"));
    }
}

