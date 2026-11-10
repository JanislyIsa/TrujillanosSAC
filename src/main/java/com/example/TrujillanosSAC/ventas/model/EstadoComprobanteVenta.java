package com.example.TrujillanosSAC.ventas.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity(name = "VentaEstadoComprobanteVenta")
@Table(name = "ESTADO_COMPROBANTE")
@Getter
@Setter
@NoArgsConstructor
public class EstadoComprobanteVenta extends Catalogo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID_ESTADO_COMPROBANTE")
    private Long id;
}
