package com.example.TrujillanosSAC.compras.model;

import com.example.TrujillanosSAC.inventario.model.Insumo;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.*;
import java.math.BigDecimal;

@Entity(name = "ComprasDetalleCotizacion")
@Table(name = "DETALLE_COTIZACION")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class DetalleCotizacion {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID_DETALLE_COT")
    private Long idDetalleCot;

    @NotNull @Positive @Column(name = "CANTIDAD", precision = 10, scale = 2, nullable = false)
    private BigDecimal cantidad;

    @NotNull @PositiveOrZero @Column(name = "PRECIO_UNITARIO", precision = 12, scale = 2, nullable = false)
    private BigDecimal precioUnitario;

    @NotNull @PositiveOrZero @Column(name = "SUBTOTAL", precision = 12, scale = 2, nullable = false)
    private BigDecimal subtotal;

    @NotNull @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "ID_COTIZACION", nullable = false)
    private Cotizacion cotizacion;

    @NotNull @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "ID_INSUMO", nullable = false)
    private Insumo insumo;
}
