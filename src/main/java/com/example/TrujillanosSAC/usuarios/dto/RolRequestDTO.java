package com.example.TrujillanosSAC.usuarios.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor
public class RolRequestDTO {
    @NotBlank @Size(max = 20) private String nombre;
}
