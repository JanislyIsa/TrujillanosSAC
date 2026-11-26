package com.example.TrujillanosSAC.compras.service.service;

import com.example.TrujillanosSAC.compras.dto.OrdenesPorProveedorReporteDTO;

import java.time.LocalDate;
import java.util.List;

public interface ReporteComprasService {
    List<OrdenesPorProveedorReporteDTO> ordenesPorProveedor(LocalDate desde, LocalDate hasta);
}
