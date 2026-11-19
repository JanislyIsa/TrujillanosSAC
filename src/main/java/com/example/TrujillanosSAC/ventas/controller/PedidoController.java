package com.example.TrujillanosSAC.ventas.controller;

import com.example.TrujillanosSAC.ventas.dto.request.CambioEstadoPedidoRequestDTO;
import com.example.TrujillanosSAC.ventas.dto.request.PedidoRequestDTO;
import com.example.TrujillanosSAC.ventas.dto.response.PedidoResponseDTO;
import com.example.TrujillanosSAC.ventas.model.codigo.EstadoPedidoCodigo;
import com.example.TrujillanosSAC.ventas.security.ActorVenta;
import com.example.TrujillanosSAC.ventas.service.service.PedidoService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
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

    /**
     * Búsqueda con filtros combinables (todos opcionales) y ordenamiento.
     * Ejemplo: /buscar?clienteId=3&amp;estado=PAGADO&amp;desde=2026-11-01T00:00:00&amp;hasta=2026-11-30T23:59:59&amp;ordenarPor=total&amp;direccion=asc
     * Sin coincidencias responde 200 con []; ordenarPor fuera de (fecha, total, id) responde 409;
     * una fecha que no sea ISO responde 400.
     */
    @GetMapping("/buscar")
    public ResponseEntity<List<PedidoResponseDTO>> buscar(
            @RequestParam(name = "clienteId", required = false) @Positive Long clienteId,
            @RequestParam(name = "estado", required = false) EstadoPedidoCodigo estado,
            @RequestParam(name = "desde", required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime desde,
            @RequestParam(name = "hasta", required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime hasta,
            @RequestParam(name = "ordenarPor", defaultValue = "fecha") String ordenarPor,
            @RequestParam(name = "direccion", defaultValue = "desc") String direccion) {
        return ResponseEntity.ok(service.buscar(clienteId, estado, desde, hasta, ordenarPor, direccion,
                ActorVenta.actual()));
    }

    @GetMapping("/{id}")
    public PedidoResponseDTO obtener(@PathVariable("id") @Positive Long id) {
        return service.obtener(id, ActorVenta.actual());
    }

    /**
     * Cancela un pedido pendiente de pago y devuelve el stock. Solo el empleado: el cliente no cancela;
     * si no paga, el pedido vence solo (ExpiracionPedidos) y el stock vuelve.
     */
    @PostMapping("/{id}/cancelar")
    @PreAuthorize("hasRole('EMPLEADO')")
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
