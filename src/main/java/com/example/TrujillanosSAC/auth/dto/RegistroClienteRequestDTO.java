package com.example.TrujillanosSAC.auth.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

/** Datos con los que un cliente crea su cuenta desde la tienda. */
public record RegistroClienteRequestDTO(
        @NotBlank(message = "El nombre es obligatorio") @Size(max = 100) String nombre,
        @NotBlank(message = "El apellido es obligatorio") @Size(max = 100) String apellido,
        @NotBlank(message = "El tipo de documento es obligatorio") @Size(max = 10) String tipoDocumento,
        @NotBlank(message = "El número de documento es obligatorio") @Size(max = 20) String numeroDocumento,
        @NotBlank(message = "El teléfono es obligatorio")
        @Pattern(regexp = "\\d{9}", message = "El teléfono debe tener 9 dígitos") String telefono,
        @NotBlank(message = "El correo es obligatorio") @Email(message = "El correo no es válido") @Size(max = 100) String correo,
        @NotBlank(message = "El usuario es obligatorio")
        @Pattern(regexp = "[A-Za-z0-9._-]{4,20}",
                message = "El usuario debe tener de 4 a 20 caracteres: letras, números, punto, guion o guion bajo") String username,
        @NotBlank(message = "La contraseña es obligatoria")
        @Size(min = 8, max = 72, message = "La contraseña debe tener entre 8 y 72 caracteres") String password) {
}
