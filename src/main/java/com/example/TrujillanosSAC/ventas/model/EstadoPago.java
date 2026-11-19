package com.example.TrujillanosSAC.ventas.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity(name = "VentaEstadoPago")
@Table(name = "ESTADO_PAGO")
@Getter
@Setter
@NoArgsConstructor
public class EstadoPago extends Catalogo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID_ESTADO_PAGO")
    private Long id;
}
