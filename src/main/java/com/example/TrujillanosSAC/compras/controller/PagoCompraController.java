package com.example.TrujillanosSAC.compras.controller;

import com.example.TrujillanosSAC.compras.dto.PagoCompraRequestDTO;
import com.example.TrujillanosSAC.compras.dto.PagoCompraResponseDTO;
import com.example.TrujillanosSAC.compras.service.service.PagoCompraService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@Validated
@RequestMapping("/api/v1/compras/pagos")
@PreAuthorize("hasAuthority('COMPRAS_PAGOS_GESTIONAR')")
public class PagoCompraController {
    private final PagoCompraService pagoCompraService;

    public PagoCompraController(PagoCompraService pagoCompraService) {
        this.pagoCompraService = pagoCompraService;
    }

    @PostMapping
    public ResponseEntity<PagoCompraResponseDTO> pagar(@Valid @RequestBody PagoCompraRequestDTO request) {
        PagoCompraResponseDTO response = pagoCompraService.create(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping
    public ResponseEntity<List<PagoCompraResponseDTO>> listar() {
        return ResponseEntity.ok((List<PagoCompraResponseDTO>) pagoCompraService.readAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<PagoCompraResponseDTO> buscar(@PathVariable @Positive Long id) {
        return ResponseEntity.ok(pagoCompraService.read(id));
    }
}
