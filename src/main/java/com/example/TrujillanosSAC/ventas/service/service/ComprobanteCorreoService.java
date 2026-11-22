package com.example.TrujillanosSAC.ventas.service.service;

import com.example.TrujillanosSAC.ventas.dto.response.EnvioComprobanteResponseDTO;
import com.example.TrujillanosSAC.ventas.security.ActorVenta;

/** Envío del comprobante de pago (ticket) por correo. */
public interface ComprobanteCorreoService {

    /**
     * Envío automático al aprobarse el pago, en segundo plano. Destino: el correo de la cuenta del cliente
     * o, si no tiene, el que ingresó al pagar. Nunca lanza excepciones: un fallo solo queda en el log.
     */
    void enviarAlAprobarse(Long pedidoId, String correoPago);

    /** Reenvío a pedido del usuario ("Enviar a mi correo"). Valida acceso, que esté pagado y que haya correo. */
    EnvioComprobanteResponseDTO reenviar(Long pedidoId, ActorVenta actor);
}
