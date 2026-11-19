package com.example.TrujillanosSAC.inventario.dto.request;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;
import java.math.BigDecimal;

public record ProductoRequest(
        @NotBlank @Size(max = 100) String nombre,
        @NotBlank @Size(max = 255) String descripcion,
        @NotNull @DecimalMin("0.01") @Digits(integer = 8, fraction = 2) BigDecimal precio,
        @PositiveOrZero Integer stock,
        @NotNull @PositiveOrZero Integer stockMinimo,
        @NotNull @PositiveOrZero Integer stockLimite,
        @NotNull @Positive Long idCategoria,
        @NotNull @Positive Long idPresentacion,
        @NotNull @Positive Long idAlmacen,
        @Size(max = 255) String imagenRuta,
        Boolean activo
) {
    public ProductoRequest(
            String nombre,
            String descripcion,
            BigDecimal precio,
            Integer stock,
            Integer stockMinimo,
            Integer stockLimite,
            Long idCategoria,
            Long idPresentacion,
            Long idAlmacen,
            String imagenRuta
    ) {
        this(nombre, descripcion, precio, stock, stockMinimo, stockLimite,
                idCategoria, idPresentacion, idAlmacen, imagenRuta, null);
    }
}
