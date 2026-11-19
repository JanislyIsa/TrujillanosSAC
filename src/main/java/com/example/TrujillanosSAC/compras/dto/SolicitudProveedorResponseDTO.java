package com.example.TrujillanosSAC.compras.dto;

import java.time.LocalDate;
import java.util.List;

public record SolicitudProveedorResponseDTO(Long id, LocalDate fecha, String estado, List<InsumoSolicitado> insumos) {
    public record InsumoSolicitado(Long id, String nombre, Integer cantidad, String unidad) {}
}
