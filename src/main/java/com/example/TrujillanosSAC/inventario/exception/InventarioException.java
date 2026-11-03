package com.example.TrujillanosSAC.inventario.exception;
import org.springframework.http.HttpStatus;

public class InventarioException extends RuntimeException {
    private final HttpStatus estado;
    public InventarioException(HttpStatus estado, String mensaje) {
        super(mensaje); this.estado = estado;
    }
    public HttpStatus getEstado() { return estado; }
    public static InventarioException noEncontrado(String nombre) {
        return new InventarioException(HttpStatus.NOT_FOUND, nombre + " no encontrado");
    }
    public static InventarioException conflicto(String mensaje) {
        return new InventarioException(HttpStatus.CONFLICT, mensaje);
    }
}
