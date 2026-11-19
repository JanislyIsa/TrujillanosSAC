package com.example.TrujillanosSAC.inventario.dto.request;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record SolicitudAutomaticaRequest(
        @NotNull @Positive Long idProveedor
) {
}
