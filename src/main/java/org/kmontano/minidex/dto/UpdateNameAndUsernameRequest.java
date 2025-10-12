package org.kmontano.minidex.dto;


import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * Request DTO para actualizar el nombre y el username de un entrenador.
 */
public class UpdateNameAndUsernameRequest {
    @NotBlank(message = "El nombre no puede estar vacío")
    @Size(max = 50, message = "El nombre no puede superar los 50 caracteres")
    private String name;

    @NotBlank(message = "El username no puede estar vacío")
    @Size(min = 3, max = 30, message = "El username debe tener entre 3 y 30 caracteres")
    private String username;

    // Getters y setters
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }
}
