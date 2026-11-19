package com.example.TrujillanosSAC.ventas.service.pasarela;

/** montoCentimos: S/ 25.50 se envía como 2550, que es como lo pide Culqi. */
public record SolicitudCobro(long montoCentimos, String moneda, String email, String token, String descripcion) {
}
