package com.example.TrujillanosSAC.compras.dto;

import java.math.BigDecimal;

public record OrdenesResumenResponseDTO(long total, long pendientes, BigDecimal montoPendiente) {}
