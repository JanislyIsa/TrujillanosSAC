package com.example.TrujillanosSAC.ventas.model.codigo;

public enum MetodoPagoCodigo implements CodigoCatalogo {
    TARJETA("Tarjeta"),
    YAPE("Yape");

    private final String nombre;

    MetodoPagoCodigo(String nombre) { this.nombre = nombre; }

    @Override public String nombre() { return nombre; }
}
