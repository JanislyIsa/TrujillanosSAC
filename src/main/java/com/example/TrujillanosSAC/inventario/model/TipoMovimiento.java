package com.example.TrujillanosSAC.inventario.model;
public enum TipoMovimiento {
    INGRESO("Ingreso"), SALIDA("Salida");
    private final String valor;
    TipoMovimiento(String valor) { this.valor = valor; }
    public String valor() { return valor; }
}
