package com.example.TrujillanosSAC.inventario.dto.request;

import com.example.TrujillanosSAC.inventario.model.TipoItem;
import com.example.TrujillanosSAC.inventario.model.TipoMovimiento;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

public record MovimientoRequest(
        @NotNull TipoMovimiento tipoMovimiento,
        @NotNull TipoItem tipoItem,
        @NotNull @Positive Long idItem,
        @NotNull @Positive Integer cantidad,
        @NotBlank @Size(max = 200) String motivo
) {
}
