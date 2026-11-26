package com.example.TrujillanosSAC.reportes.controller;

import com.example.TrujillanosSAC.reportes.dto.MovimientoInventarioReporteDTO;
import com.example.TrujillanosSAC.reportes.dto.OrdenesPorProveedorReporteDTO;
import com.example.TrujillanosSAC.reportes.service.ReporteService;
import com.example.TrujillanosSAC.ventas.dto.reporte.VentaPorCategoriaDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/reportes")
@PreAuthorize("hasRole('EMPLEADO')")
public class ReporteController {

    private final ReporteService reportes;

    @GetMapping("/ventas-por-categoria")
    public ResponseEntity<List<VentaPorCategoriaDTO>> ventasPorCategoria(
            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime desde,
            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime hasta) {
        return ResponseEntity.ok(reportes.ventasPorCategoria(desde, hasta));
    }

    @GetMapping("/compras-por-proveedor")
    @PreAuthorize("hasRole('EMPLEADO') and hasAuthority('COMPRAS_ORDENES_GESTIONAR')")
    public ResponseEntity<List<OrdenesPorProveedorReporteDTO>> ordenesPorProveedor(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate desde,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate hasta) {
        return ResponseEntity.ok(reportes.ordenesPorProveedor(desde, hasta));
    }

    @GetMapping("/inventario-movimientos")
    public ResponseEntity<List<MovimientoInventarioReporteDTO>> movimientosInventario(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate desde,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate hasta) {
        return ResponseEntity.ok(reportes.movimientosInventario(desde, hasta));
    }
}
