package com.example.TrujillanosSAC.ventas.model;

import jakarta.persistence.Column;
import jakarta.persistence.MappedSuperclass;
import lombok.Getter;
import lombok.Setter;

/**
 * Campos comunes de los catálogos de ventas (estados, tipos y métodos de pago).
 * <p>
 * CODIGO lo usa el programa para encontrar un registro y nunca cambia (por ejemplo "PAGADO").
 * NOMBRE es el texto que ve el usuario y sí se puede editar (por ejemplo "Pagado").
 */
@MappedSuperclass
@Getter
@Setter
public abstract class Catalogo {

    @Column(name = "CODIGO", length = 30, nullable = false, unique = true, updatable = false)
    private String codigo;

    @Column(name = "NOMBRE", length = 50, nullable = false)
    private String nombre;

    public abstract Long getId();
}
