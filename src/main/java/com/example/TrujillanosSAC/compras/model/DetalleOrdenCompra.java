package com.example.TrujillanosSAC.compras.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.*;
import java.math.BigDecimal;

@Entity
@Table(name = "DETALLE_ORDEN_COMPRA")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class DetalleOrdenCompra {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID_DETALLE_ORDEN")
    private Long idDetalleOrden;

    @NotNull @Positive @Column(name = "CANTIDAD", precision = 10, scale = 2, nullable = false)
    private BigDecimal cantidad;

    @NotNull @PositiveOrZero @Column(name = "PRECIO_UNITARIO", precision = 12, scale = 2, nullable = false)
    private BigDecimal precioUnitario;

    @NotNull @PositiveOrZero @Column(name = "SUBTOTAL", precision = 12, scale = 2, nullable = false)
    private BigDecimal subtotal;

    @NotNull @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "ID_ORDEN_COMPRA", nullable = false)
    private OrdenCompra ordenCompra;

    @NotNull @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "ID_DETALLE_COT", nullable = false)
    private DetalleCotizacion detalleCotizacion;
}
