package com.example.TrujillanosSAC.ventas.model.codigo;

public enum TipoComprobanteCodigo implements CodigoCatalogo {
    BOLETA("Boleta de venta", "B001"),
    FACTURA("Factura", "F001");

    private final String nombre;
    private final String serie;

    TipoComprobanteCodigo(String nombre, String serie) {
        this.nombre = nombre;
        this.serie = serie;
    }

    @Override public String nombre() { return nombre; }

    public String serie() { return serie; }
}
