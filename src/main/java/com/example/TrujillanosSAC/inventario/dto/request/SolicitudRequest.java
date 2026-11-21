package com.example.TrujillanosSAC.inventario.dto.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import java.util.List;

public record SolicitudRequest(
        @NotNull @Positive Long idProveedor,
        @NotEmpty @Size(max = 100) List<@NotNull @Valid SolicitudLineaRequest> detalles
) {
}
