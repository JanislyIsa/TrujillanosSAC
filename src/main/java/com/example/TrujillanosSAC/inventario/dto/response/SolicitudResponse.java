package com.example.TrujillanosSAC.inventario.dto.response;

import java.time.LocalDate;
import java.util.List;

public record SolicitudResponse(
        Long id,
        String codigo,
        LocalDate fecha,
        String estado,
        Long idProveedor,
        Long idEmpleado,
        List<SolicitudDetalleResponse> detalles
) {
}
