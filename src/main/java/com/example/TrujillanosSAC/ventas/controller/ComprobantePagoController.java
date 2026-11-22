package com.example.TrujillanosSAC.ventas.controller;

import com.example.TrujillanosSAC.ventas.dto.response.ComprobantePagoDTO;
import com.example.TrujillanosSAC.ventas.security.ActorVenta;
import com.example.TrujillanosSAC.ventas.service.service.ComprobantePagoService;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/** Comprobante de pago (ticket) de un pedido. El cliente solo accede a sus pedidos; el empleado, a todos. */
@RestController("ventasComprobantePagoController")
@RequestMapping("/api/v1/ventas/pedidos/{id}")
@PreAuthorize("hasAnyRole('EMPLEADO','CLIENTE')")
@RequiredArgsConstructor
public class ComprobantePagoController {

    private final ComprobantePagoService service;

    /** Datos del ticket de un pedido pagado. 409 si el pedido todavía no está pagado. */
    @GetMapping("/comprobante-pago")
    public ComprobantePagoDTO obtener(@PathVariable("id") @Positive Long id) {
        return service.obtener(id, ActorVenta.actual());
    }
}
