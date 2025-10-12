package org.kmontano.minidex.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;

/**
 * Request DTO para actualizar las monedas de un entrenador.
 */
public class UpdateCoinsRequest {
    @NotNull(message = "Las monedas no pueden ser nulas")
    @Min(value = 1, message = "Las monedas deben ser al menos 1")
    private Integer coins;

    @NotNull(message = "La acción no puede ser nula")
    @Pattern(regexp = "add|subtract", message = "La acción debe ser 'add' o 'subtract'")
    private String action;

    // Getters y setters
    public Integer getCoins() {
        return coins;
    }

    public void setCoins(Integer coins) {
        this.coins = coins;
    }

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }
}
