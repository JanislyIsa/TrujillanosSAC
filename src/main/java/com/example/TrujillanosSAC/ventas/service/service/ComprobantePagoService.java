package com.example.TrujillanosSAC.ventas.service.service;

import com.example.TrujillanosSAC.ventas.dto.response.ComprobantePagoDTO;
import com.example.TrujillanosSAC.ventas.security.ActorVenta;

/** Datos del ticket (comprobante de pago) de un pedido pagado. */
public interface ComprobantePagoService {

    /** Para el endpoint: el cliente solo ve sus pedidos; el empleado, cualquiera. */
    ComprobantePagoDTO obtener(Long pedidoId, ActorVenta actor);

    /** Uso interno (correo del comprobante): sin control de acceso, quien llama ya lo validó. */
    ComprobantePagoDTO armar(Long pedidoId);
}
