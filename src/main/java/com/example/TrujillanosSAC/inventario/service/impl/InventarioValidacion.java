package com.example.TrujillanosSAC.inventario.service.impl;
import jakarta.validation.*;
import org.springframework.stereotype.Component;

@Component
public class InventarioValidacion {
    private final Validator validator;
    public InventarioValidacion(Validator validator) { this.validator = validator; }
    public <T> void validar(T valor) {
        if (valor == null) throw new IllegalArgumentException("El cuerpo es obligatorio");
        var errores = validator.validate(valor);
        if (!errores.isEmpty()) throw new ConstraintViolationException(errores);
    }
    public void limites(Integer minimo, Integer limite) {
        if (minimo == null || limite == null || minimo < 0 || limite < minimo)
            throw new IllegalArgumentException("El limite debe ser mayor o igual al minimo y ambos no negativos");
    }
}
