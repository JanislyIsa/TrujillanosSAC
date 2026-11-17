package com.example.TrujillanosSAC.compras.dto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public record SolicitudProveedorResponseDTO(Long id, LocalDate fecha, String estado, List<InsumoSolicitado> insumos) {
    public record InsumoSolicitado(Long id, String nombre, BigDecimal cantidad, String unidad) {}
}
