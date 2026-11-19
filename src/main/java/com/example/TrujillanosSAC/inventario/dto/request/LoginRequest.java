package com.example.TrujillanosSAC.inventario.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record LoginRequest(
        @NotBlank @Size(max = 20) String username,
        @NotBlank @Size(max = 255) String password
) {
}
