package com.example.TrujillanosSAC.auth.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record CambiarClaveRequestDTO(
        @NotBlank(message = "La contraseña actual es obligatoria") @Size(max = 72) String claveActual,
        @NotBlank(message = "La nueva contraseña es obligatoria")
        @Size(min = 8, max = 72, message = "La contraseña debe tener entre 8 y 72 caracteres") String nuevaClave) {
}
