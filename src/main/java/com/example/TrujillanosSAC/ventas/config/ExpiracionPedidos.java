package com.example.TrujillanosSAC.ventas.config;

import com.example.TrujillanosSAC.ventas.service.PedidoService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

/**
 * El stock se reserva al crear el pedido. Si el cliente no paga en ventas.pedido.expiracion.minutos,
 * esta tarea cancela el pedido y devuelve el stock, para que no quede retenido para siempre.
 */
@Component
@ConditionalOnProperty(name = "ventas.pedido.expiracion.habilitado", havingValue = "true", matchIfMissing = true)
public class ExpiracionPedidos {

    private static final Logger log = LoggerFactory.getLogger(ExpiracionPedidos.class);

    private final PedidoService pedidos;
    private final long minutos;

    public ExpiracionPedidos(PedidoService pedidos,
                             @Value("${ventas.pedido.expiracion.minutos:30}") long minutos) {
        this.pedidos = pedidos;
        this.minutos = minutos;
    }

    @Scheduled(fixedDelayString = "${ventas.pedido.expiracion.revision-ms:60000}",
            initialDelayString = "${ventas.pedido.expiracion.revision-ms:60000}")
    public void cancelarPedidosVencidos() {
        int cancelados = pedidos.cancelarVencidos(LocalDateTime.now().minusMinutes(minutos));
        if (cancelados > 0) {
            log.info("Pedidos cancelados por falta de pago: {}", cancelados);
        }
    }
}
