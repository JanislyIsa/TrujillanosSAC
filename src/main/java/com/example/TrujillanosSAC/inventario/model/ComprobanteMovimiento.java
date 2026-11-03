package com.example.TrujillanosSAC.inventario.model;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

/** Un comprobante durable por movimiento; comparte su clave primaria. */
@Entity
@Table(name = "COMPROBANTE_MOVIMIENTO")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class ComprobanteMovimiento {
    @Id
    @Column(name = "ID_MOVIMIENTO")
    private Long idMovimiento;

    @MapsId
    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "ID_MOVIMIENTO", nullable = false)
    private Movimiento movimiento;

    @Column(name = "CODIGO", length = 30, nullable = false, unique = true)
    private String codigo;

    @Column(name = "FECHA_EMISION", nullable = false)
    private LocalDateTime fechaEmision;

    @Column(name = "RESPONSABLE", length = 100)
    private String responsable;

    @Column(name = "MOTIVO", length = 200)
    private String motivo;
}
