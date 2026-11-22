package com.example.TrujillanosSAC.auth.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/** El token llega en el enlace del correo (?token=...). */
public record RestablecerClaveRequestDTO(
        @NotBlank(message = "El enlace no es válido") @Size(max = 100) String token,
        @NotBlank(message = "La nueva contraseña es obligatoria")
        @Size(min = 8, max = 72, message = "La contraseña debe tener entre 8 y 72 caracteres") String nuevaClave) {
}
