package com.example.TrujillanosSAC.compras.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDate;

@Entity(name = "ComprasComprobanteCompra")
@Table(name = "COMPROBANTE_COMPRA")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class ComprobanteCompra {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID_COMPROBANTE")
    private Long idComprobante;

    @NotNull @Column(name = "FECHA_EMISION", nullable = false)
    private LocalDate fechaEmision;

    @NotNull @PositiveOrZero @Column(name = "MONTO", precision = 12, scale = 2, nullable = false)
    private BigDecimal monto;

    @Column(name = "NUMERO_FACTURA", length = 50)
    private String numeroFactura;

    @Lob @Basic(fetch = FetchType.LAZY) @Column(name = "ARCHIVO_FACTURA")
    private byte[] archivoFactura;

    @NotNull @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "ID_ESTADO", nullable = false)
    private EstadoComprobanteCompra estadoComprobanteCompra;

    @NotNull @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "ID_ORDEN_COMPRA", nullable = false)
    private OrdenCompra ordenCompra;
}
