package com.example.TrujillanosSAC.inventario.model;
public enum EstadoStock {
    BAJO, NORMAL, SOBRESTOCK;
    public static EstadoStock calcular(int stock, int minimo, int limite) {
        if (stock < minimo) return BAJO;
        if (stock > limite) return SOBRESTOCK;
        return NORMAL;
    }
}
