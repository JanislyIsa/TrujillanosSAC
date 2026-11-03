package com.example.TrujillanosSAC.inventario.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Entity
@Table(name = "DETALLE_SOLICITUD")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DetalleSolicitud {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID_DETALLE_SOL")
    private Long idDetalleSol;

    @NotNull
    @Column(name = "CANTIDAD", precision = 10, scale = 2, nullable = false)
    private BigDecimal cantidad;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ID_SOLICITUD", nullable = false)
    private SolicitudCompra solicitudCompra;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ID_INSUMO", nullable = false)
    private Insumo insumo;
}
