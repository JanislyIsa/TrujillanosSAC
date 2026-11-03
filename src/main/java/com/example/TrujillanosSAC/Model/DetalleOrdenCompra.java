package com.example.TrujillanosSAC.Model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Entity
@Table(name = "DETALLE_ORDEN_COMPRA")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DetalleOrdenCompra {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID_DETALLE_ORDEN")
    private Long idDetalleOrden;

    @NotNull
    @Column(name = "CANTIDAD", precision = 10, scale = 2, nullable = false)
    private BigDecimal cantidad;

    @NotNull
    @Column(name = "PRECIO_UNITARIO", precision = 12, scale = 2, nullable = false)
    private BigDecimal precioUnitario;

    @NotNull
    @Column(name = "SUBTOTAL", precision = 12, scale = 2, nullable = false)
    private BigDecimal subtotal;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ID_ORDEN_COMPRA", nullable = false)
    private OrdenCompra ordenCompra;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ID_DETALLE_COT", nullable = false)
    private DetalleCotizacion detalleCotizacion;
}
