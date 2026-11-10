package com.example.TrujillanosSAC.ventas.service.pasarela;

/**
 * Cobro con tarjeta. Hay dos implementaciones y se elige con la propiedad ventas.pago.pasarela:
 * "culqi" ({@link CulqiPasarela}) o "simulada" ({@link PasarelaSimulada}, la de por defecto).
 */
public interface PasarelaPago {
    ResultadoCobro cobrar(SolicitudCobro solicitud);
}
