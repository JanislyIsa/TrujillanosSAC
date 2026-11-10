package com.example.TrujillanosSAC.ventas.controller;

import com.example.TrujillanosSAC.ventas.dto.request.CambioEstadoPedidoRequestDTO;
import com.example.TrujillanosSAC.ventas.dto.request.PedidoRequestDTO;
import com.example.TrujillanosSAC.ventas.dto.response.PedidoResponseDTO;
import com.example.TrujillanosSAC.ventas.service.ActorVenta;
import com.example.TrujillanosSAC.ventas.service.PedidoService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController("ventasPedidoController")
@RequestMapping("/api/v1/ventas/pedidos")
@PreAuthorize("hasAnyRole('EMPLEADO','CLIENTE')")
@RequiredArgsConstructor
public class PedidoController {

    private final PedidoService service;

    @PostMapping
    @PreAuthorize("hasRole('CLIENTE')")
    public ResponseEntity<PedidoResponseDTO> crear(@Valid @RequestBody PedidoRequestDTO request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.crear(request, ActorVenta.actual()));
    }

    /** Un empleado ve todos los pedidos; un cliente, solo los suyos. */
    @GetMapping
    public List<PedidoResponseDTO> listar() {
        return service.listar(ActorVenta.actual());
    }

    @GetMapping("/{id}")
    public PedidoResponseDTO obtener(@PathVariable("id") @Positive Long id) {
        return service.obtener(id, ActorVenta.actual());
    }

    /** Cancela un pedido pendiente de pago y devuelve el stock. */
    @PostMapping("/{id}/cancelar")
    public PedidoResponseDTO cancelar(@PathVariable("id") @Positive Long id) {
        return service.cancelar(id, ActorVenta.actual());
    }

    /** Avance manual: PAGADO → LISTO_PARA_RECOJO → ENTREGADO. */
    @PatchMapping("/{id}/estado")
    @PreAuthorize("hasRole('EMPLEADO')")
    public PedidoResponseDTO cambiarEstado(@PathVariable("id") @Positive Long id,
                                           @Valid @RequestBody CambioEstadoPedidoRequestDTO request) {
        return service.cambiarEstado(id, request.estado());
    }
}
