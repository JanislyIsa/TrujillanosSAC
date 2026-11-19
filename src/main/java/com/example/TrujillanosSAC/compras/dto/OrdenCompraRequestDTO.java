package com.example.TrujillanosSAC.compras.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

public record OrdenCompraRequestDTO(
        @NotNull @Positive Long cotizacionId,
        @Size(max = 300) String descripcion
) {}
