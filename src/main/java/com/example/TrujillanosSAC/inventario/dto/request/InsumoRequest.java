package com.example.TrujillanosSAC.inventario.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;

public record InsumoRequest(
        @NotBlank @Size(max = 100) String nombre,
        @PositiveOrZero Integer stock,
        @NotNull @PositiveOrZero Integer stockMinimo,
        @NotNull @PositiveOrZero Integer stockLimite,
        @NotNull @Positive Long idUnidadMedida,
        @NotNull @Positive Long idAlmacen,
        Boolean activo
) {
    public InsumoRequest(
            String nombre,
            Integer stock,
            Integer stockMinimo,
            Integer stockLimite,
            Long idUnidadMedida,
            Long idAlmacen
    ) {
        this(nombre, stock, stockMinimo, stockLimite, idUnidadMedida, idAlmacen, null);
    }
}
