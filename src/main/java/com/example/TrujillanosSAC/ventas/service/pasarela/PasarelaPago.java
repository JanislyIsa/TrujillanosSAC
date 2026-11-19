package com.example.TrujillanosSAC.ventas.service.pasarela;

/**
 * Cobro con tarjeta o Yape. La implementación es {@link CulqiPasarela}.
 */
public interface PasarelaPago {
    ResultadoCobro cobrar(SolicitudCobro solicitud);
}
