package com.example.TrujillanosSAC.ventas.controller;

import com.example.TrujillanosSAC.ventas.dto.request.PagoCulqiRequestDTO;
import com.example.TrujillanosSAC.ventas.dto.response.PagoResponseDTO;
import com.example.TrujillanosSAC.ventas.service.ActorVenta;
import com.example.TrujillanosSAC.ventas.service.PagoService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController("ventasPagoController")
@RequestMapping("/api/v1/ventas/pagos")
@PreAuthorize("hasAnyRole('EMPLEADO','CLIENTE')")
@RequiredArgsConstructor
public class PagoController {

    private final PagoService service;

    /**
     * Pago con Culqi (tarjeta o Yape). La respuesta trae el estado del pago (APROBADO, RECHAZADO o ERROR):
     * el frontend debe revisarlo, porque un rechazo también se guarda como intento de pago.
     */
    @PostMapping("/culqi")
    @PreAuthorize("hasRole('CLIENTE')")
    public ResponseEntity<PagoResponseDTO> pagarConCulqi(@Valid @RequestBody PagoCulqiRequestDTO request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.pagarConCulqi(request, ActorVenta.actual()));
    }

    @GetMapping
    public List<PagoResponseDTO> listar(@RequestParam(name = "pedidoId", required = false) @Positive Long pedidoId) {
        return service.listar(pedidoId, ActorVenta.actual());
    }
}
