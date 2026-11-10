package com.example.TrujillanosSAC.ventas.controller;

import com.example.TrujillanosSAC.ventas.dto.request.ComprobanteVentaRequestDTO;
import com.example.TrujillanosSAC.ventas.dto.response.ComprobanteVentaResponseDTO;
import com.example.TrujillanosSAC.ventas.service.ActorVenta;
import com.example.TrujillanosSAC.ventas.service.ComprobanteVentaService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController("ventasComprobanteVentaController")
@RequestMapping("/api/v1/ventas/comprobantes")
@PreAuthorize("hasAnyRole('EMPLEADO','CLIENTE')")
@RequiredArgsConstructor
public class ComprobanteVentaController {

    private final ComprobanteVentaService service;

    @PostMapping
    public ResponseEntity<ComprobanteVentaResponseDTO> emitir(@Valid @RequestBody ComprobanteVentaRequestDTO request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.emitir(request, ActorVenta.actual()));
    }

    @GetMapping
    @PreAuthorize("hasRole('EMPLEADO')")
    public List<ComprobanteVentaResponseDTO> listar() {
        return service.listar();
    }

    @GetMapping("/{id}")
    public ComprobanteVentaResponseDTO obtener(@PathVariable("id") @Positive Long id) {
        return service.obtener(id, ActorVenta.actual());
    }
}
