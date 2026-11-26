package com.example.TrujillanosSAC.compras.controller;

import com.example.TrujillanosSAC.compras.dto.OrdenesPorProveedorReporteDTO;
import com.example.TrujillanosSAC.compras.service.service.ReporteComprasService;
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
@RequestMapping("/api/v1/compras/reportes")
@PreAuthorize("hasAuthority('COMPRAS_ORDENES_GESTIONAR')")
public class ReporteComprasController {

    private final ReporteComprasService reportes;

    @GetMapping("/ordenes-por-proveedor")
    public ResponseEntity<List<OrdenesPorProveedorReporteDTO>> ordenesPorProveedor(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate desde,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate hasta) {
        return ResponseEntity.ok(reportes.ordenesPorProveedor(desde, hasta));
    }
}
