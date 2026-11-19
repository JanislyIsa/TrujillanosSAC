package com.example.TrujillanosSAC.ventas.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * METODO_PAGO también lo usa el módulo de compras, que registra métodos sin código.
 * Por eso aquí CODIGO admite nulos: ventas usa TARJETA y YAPE (ambos cobrados por Culqi).
 */
@Entity(name = "VentaMetodoPago")
@Table(name = "METODO_PAGO")
@AttributeOverride(name = "codigo", column = @Column(name = "CODIGO", length = 30, unique = true))
@Getter
@Setter
@NoArgsConstructor
public class MetodoPago extends Catalogo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID_METODO")
    private Long id;
}
