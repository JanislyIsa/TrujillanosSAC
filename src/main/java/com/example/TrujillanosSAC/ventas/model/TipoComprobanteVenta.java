package com.example.TrujillanosSAC.ventas.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity(name = "VentaTipoComprobanteVenta")
@Table(name = "TIPO_COMPROBANTE")
@Getter
@Setter
@NoArgsConstructor
public class TipoComprobanteVenta extends Catalogo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID_TIPO_COMPROBANTE")
    private Long id;
}
