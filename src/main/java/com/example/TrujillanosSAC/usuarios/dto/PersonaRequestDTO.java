package com.example.TrujillanosSAC.usuarios.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.*;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor
public class PersonaRequestDTO {
    @NotBlank @Size(max = 100) private String nombre;
    @NotBlank @Size(max = 100) private String apellido;
    @NotBlank @Pattern(regexp = "\\d{9}") private String telefono;
    @NotNull @Positive private Long tipoDocumentoId;
    @NotBlank @Size(max = 20) private String numeroDocumento;
}
