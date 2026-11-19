package com.example.TrujillanosSAC.usuarios.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.*;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor
public class ProveedorRequestDTO {
    @NotNull @Positive private Long personaId;
    @NotBlank @Email @Size(max = 100) private String correo;
    @NotBlank @Size(max = 100) private String direccion;
}
