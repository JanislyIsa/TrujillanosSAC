package com.example.TrujillanosSAC.compras.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import java.time.LocalDate;
import java.util.List;

public record CotizacionRequestDTO(
        @NotNull @Positive Long solicitudId,
        @NotNull @Positive Long proveedorId,
        @NotNull LocalDate fechaEntrega,
        @NotEmpty @Valid List<DetalleCotizacionRequestDTO> detalles,
        @jakarta.validation.constraints.Size(max = 500) String descripcion
) {
    public CotizacionRequestDTO(Long solicitudId, Long proveedorId, LocalDate fechaEntrega,
                                List<DetalleCotizacionRequestDTO> detalles) {
        this(solicitudId, proveedorId, fechaEntrega, detalles, null);
    }
}
