package com.example.TrujillanosSAC.Dto;

import java.time.LocalDate;
import java.util.List;

public record ProveedorSolicitudDto(
        Long idSolicitud,
        String codigo,
        LocalDate fechaSolicitud,
        String estado,
        List<ProveedorSolicitudDetalleDto> detalles
) {
}
