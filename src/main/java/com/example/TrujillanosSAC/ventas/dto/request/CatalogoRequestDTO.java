package com.example.TrujillanosSAC.ventas.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/** Solo se puede cambiar el nombre visible. El código lo fija el sistema. */
public record CatalogoRequestDTO(
        @NotBlank @Size(max = 50) String nombre
) {
}
