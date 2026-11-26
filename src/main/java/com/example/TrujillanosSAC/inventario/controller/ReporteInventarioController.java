package com.example.TrujillanosSAC.inventario.controller;

import com.example.TrujillanosSAC.inventario.dto.ReporteMovimientoInventarioDto;
import com.example.TrujillanosSAC.inventario.service.service.ReporteInventarioService;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/inventario/reportes")
@PreAuthorize("hasRole('EMPLEADO')")
public class ReporteInventarioController {

    private final ReporteInventarioService reportes;

    @GetMapping("/movimientos")
    public ResponseEntity<List<ReporteMovimientoInventarioDto>> movimientos(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate desde,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate hasta) {
        return ResponseEntity.ok(reportes.movimientos(desde, hasta));
    }
}
