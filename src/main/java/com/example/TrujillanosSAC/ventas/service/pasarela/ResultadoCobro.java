package com.example.TrujillanosSAC.ventas.service.pasarela;

import com.example.TrujillanosSAC.ventas.model.codigo.MetodoPagoCodigo;

/**
 * APROBADO: se cobró. RECHAZADO: la pasarela respondió que no (tarjeta sin fondos, token vencido...).
 * ERROR: no hubo respuesta clara (caída, tiempo de espera).
 * metodo: con qué se pagó (tarjeta o Yape), según lo que informó la pasarela; null si no se cobró.
 */
public record ResultadoCobro(Resultado resultado, String referencia, String mensaje, MetodoPagoCodigo metodo) {

    public enum Resultado { APROBADO, RECHAZADO, ERROR }

    public static ResultadoCobro aprobado(String referencia, String mensaje, MetodoPagoCodigo metodo) {
        return new ResultadoCobro(Resultado.APROBADO, referencia, mensaje, metodo);
    }

    public static ResultadoCobro rechazado(String mensaje) {
        return new ResultadoCobro(Resultado.RECHAZADO, null, mensaje, null);
    }

    public static ResultadoCobro error(String mensaje) {
        return new ResultadoCobro(Resultado.ERROR, null, mensaje, null);
    }
}
