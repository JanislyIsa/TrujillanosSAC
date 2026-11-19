package com.example.TrujillanosSAC.compras.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import java.time.LocalDate;
import java.util.List;

/** Datos que el proveedor puede enviar; su identidad se obtiene del JWT. */
public record CotizacionProveedorRequestDTO(
        @NotNull @Positive Long solicitudId,
        @NotNull LocalDate fechaEntrega,
        @NotEmpty @Valid List<@NotNull @Valid DetalleCotizacionRequestDTO> detalles,
        @Size(max = 500) String descripcion
) {}
