package com.example.TrujillanosSAC.ventas.service.pasarela;

import com.example.TrujillanosSAC.ventas.model.codigo.MetodoPagoCodigo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

import java.util.Locale;
import java.util.UUID;

/**
 * Pasarela falsa para desarrollar y probar sin llaves de Culqi. No cobra nada.
 * Un token que contenga "rechazo" simula un rechazo, uno con "error" simula una caída
 * y uno con "yape" (o que empiece con "ype_", como los de Culqi) simula un pago con Yape (cualquier otro, con tarjeta).
 */
@Component
@ConditionalOnProperty(name = "ventas.pago.pasarela", havingValue = "simulada", matchIfMissing = true)
public class PasarelaSimulada implements PasarelaPago {

    private static final Logger log = LoggerFactory.getLogger(PasarelaSimulada.class);

    public PasarelaSimulada() {
        log.warn("Pagos en modo SIMULADO: no se cobra a ninguna tarjeta (ventas.pago.pasarela=simulada)");
    }

    @Override
    public ResultadoCobro cobrar(SolicitudCobro solicitud) {
        String token = solicitud.token().toLowerCase(Locale.ROOT);
        if (token.contains("rechazo")) {
            return ResultadoCobro.rechazado("Tarjeta rechazada (simulación)");
        }
        if (token.contains("error")) {
            return ResultadoCobro.error("Pasarela no disponible (simulación)");
        }
        MetodoPagoCodigo metodo = token.contains("yape") || token.startsWith("ype_")
                ? MetodoPagoCodigo.YAPE : MetodoPagoCodigo.TARJETA;
        return ResultadoCobro.aprobado("sim_" + UUID.randomUUID(), "Venta exitosa (simulación)", metodo);
    }
}
