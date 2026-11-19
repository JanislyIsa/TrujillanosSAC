package com.example.TrujillanosSAC.ventas.controller;

import com.example.TrujillanosSAC.ventas.dto.reporte.ProductoMasVendidoDTO;
import com.example.TrujillanosSAC.ventas.dto.reporte.VentaPorCategoriaDTO;
import com.example.TrujillanosSAC.ventas.service.service.ReporteService;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Reportes para la gerencia. Van bajo /api/v1/ventas/reportes (y no /api/v1/reportes) para quedar
 * dentro de la seguridad JWT y del CORS del módulo de ventas. Solo empleados.
 */
@RestController("ventasReporteController")
@RequestMapping("/api/v1/ventas/reportes")
@PreAuthorize("hasRole('EMPLEADO')")
@RequiredArgsConstructor
public class ReporteController {

    private final ReporteService reporteService;

    @GetMapping("/ventas-por-categoria")
    public ResponseEntity<List<VentaPorCategoriaDTO>> ventasPorCategoria(
            @RequestParam(name = "desde", required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime desde,
            @RequestParam(name = "hasta", required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime hasta) {
        return ResponseEntity.ok(reporteService.ventasPorCategoria(desde, hasta));
    }

    @GetMapping("/productos-mas-vendidos")
    public ResponseEntity<List<ProductoMasVendidoDTO>> productosMasVendidos(
            @RequestParam(name = "desde", required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime desde,
            @RequestParam(name = "hasta", required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime hasta,
            @RequestParam(name = "top", defaultValue = "10") @Min(1) @Max(100) int top) {
        return ResponseEntity.ok(reporteService.productosMasVendidos(desde, hasta, top));
    }
}
