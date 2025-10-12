package org.kmontano.minidex.dto;

import jakarta.validation.constraints.NotBlank;

/**
 * DTO de respuesta para autenticación.
 * Contiene el token JWT generado y los datos del Trainer.
 */
public class AuthResponse {
    @NotBlank
    private String token;

    @NotBlank
    private TrainerDTO trainer;

    public AuthResponse(String token, TrainerDTO trainerDTO) { this.token = token; this.trainer = trainerDTO; }

    public String getToken() { return token; }
    public TrainerDTO getTrainerDTO() {return trainer;}
}