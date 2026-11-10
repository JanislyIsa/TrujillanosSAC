package com.example.TrujillanosSAC.auth.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record LoginRequestDTO(
        @NotBlank(message = "El usuario es obligatorio") @Size(max = 20) String username,
        @NotBlank(message = "La contraseña es obligatoria") @Size(max = 255) String password) {
}
