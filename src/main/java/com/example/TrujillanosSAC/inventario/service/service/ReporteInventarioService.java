package com.example.TrujillanosSAC.inventario.service.service;

import com.example.TrujillanosSAC.inventario.dto.ReporteMovimientoInventarioDto;

import java.time.LocalDate;
import java.util.List;

public interface ReporteInventarioService {
    List<ReporteMovimientoInventarioDto> movimientos(LocalDate desde, LocalDate hasta);
}
