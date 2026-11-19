package com.example.TrujillanosSAC.ventas.model;

import com.example.TrujillanosSAC.ventas.model.codigo.EstadoSunat;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity(name = "VentaComprobanteVenta")
@Table(
        name = "COMPROBANTE_VENTA",
        uniqueConstraints = @UniqueConstraint(
                name = "UK_COMPROBANTE_TIPO_SERIE_NUMERO",
                columnNames = {"ID_TIPO_COMPROBANTE", "SERIE", "NUMERO"}
        )
)
@Getter
@Setter
@NoArgsConstructor
public class ComprobanteVenta {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID_COMPROBANTE")
    private Long idComprobante;

    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "ID_PEDIDO", nullable = false, unique = true)
    private Pedido pedido;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "ID_TIPO_COMPROBANTE", nullable = false)
    private TipoComprobanteVenta tipo;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "ID_ESTADO_COMPROBANTE", nullable = false)
    private EstadoComprobanteVenta estado;

    @Column(name = "SERIE", length = 4, nullable = false)
    private String serie;

    @Column(name = "NUMERO", length = 8, nullable = false)
    private String numero;

    @Column(name = "FECHA_EMISION", nullable = false)
    private LocalDateTime fechaEmision;

    @Column(name = "TOTAL", precision = 10, scale = 2, nullable = false)
    private BigDecimal total;

    @Enumerated(EnumType.STRING)
    @Column(name = "ESTADO_SUNAT", length = 20, nullable = false)
    private EstadoSunat estadoSunat = EstadoSunat.NO_ENVIADO;
}
