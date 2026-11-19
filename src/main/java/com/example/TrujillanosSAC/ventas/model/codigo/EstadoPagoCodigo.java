package com.example.TrujillanosSAC.ventas.model.codigo;

public enum EstadoPagoCodigo implements CodigoCatalogo {
    PROCESANDO("Procesando"),
    APROBADO("Aprobado"),
    RECHAZADO("Rechazado"),
    ERROR("Error de comunicación");

    private final String nombre;

    EstadoPagoCodigo(String nombre) { this.nombre = nombre; }

    @Override public String nombre() { return nombre; }
}
