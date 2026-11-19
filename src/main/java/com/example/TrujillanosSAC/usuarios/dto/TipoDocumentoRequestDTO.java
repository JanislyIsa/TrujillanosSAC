package com.example.TrujillanosSAC.usuarios.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.*;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor
public class TipoDocumentoRequestDTO {
    @NotBlank @Size(max = 20) private String nombre;
    @NotBlank @Size(max = 10) private String abreviatura;
    @NotNull @Positive private Integer longitud;
}
