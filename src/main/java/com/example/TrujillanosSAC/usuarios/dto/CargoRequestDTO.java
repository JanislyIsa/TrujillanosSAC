package com.example.TrujillanosSAC.usuarios.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor
public class CargoRequestDTO {
    @NotBlank @Size(max = 50) private String nombre;
}
