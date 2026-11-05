package com.example.TrujillanosSAC.compras.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Entity
@Table(name = "ESTADO_COMPROBANTE_COMPRA")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class EstadoComprobanteCompra {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID_ESTADO")
    private Long idEstadoComprobanteCompra;

    @NotBlank
    @Column(name = "NOMBRE", length = 50, nullable = false, unique = true)
    private String nombre;
}
