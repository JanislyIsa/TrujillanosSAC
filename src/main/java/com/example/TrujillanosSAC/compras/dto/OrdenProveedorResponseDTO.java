package com.example.TrujillanosSAC.compras.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

public record OrdenProveedorResponseDTO(
        Long id, LocalDate fecha, Long cotizacionId, BigDecimal total,
        String estado, Factura factura) {
    public record Factura(Long id, String numero, LocalDate fecha, BigDecimal monto, String estado) {}
}
