package com.example.TrujillanosSAC.ventas.model.codigo;

public enum EstadoComprobanteCodigo implements CodigoCatalogo {
    EMITIDO("Emitido");

    private final String nombre;

    EstadoComprobanteCodigo(String nombre) { this.nombre = nombre; }

    @Override public String nombre() { return nombre; }
}
