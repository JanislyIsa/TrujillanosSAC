package com.example.TrujillanosSAC.auth.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/** Correo con el que el cliente se registró. */
public record OlvideClaveRequestDTO(
        @NotBlank(message = "El correo es obligatorio")
        @Email(message = "El correo no es válido")
        @Size(max = 100) String correo) {
}
