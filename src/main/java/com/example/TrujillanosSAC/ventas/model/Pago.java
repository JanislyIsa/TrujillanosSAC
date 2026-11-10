package com.example.TrujillanosSAC.ventas.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/** Un intento de pago de un pedido. Nunca guarda datos de la tarjeta: solo la referencia que devuelve la pasarela. */
@Entity(name = "VentaPago")
@Table(name = "PAGO_VENTA")
@Getter
@Setter
@NoArgsConstructor
public class Pago {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID_PAGO")
    private Long idPago;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "ID_PEDIDO", nullable = false)
    private Pedido pedido;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "ID_METODO", nullable = false)
    private MetodoPago metodoPago;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "ID_ESTADO_PAGO", nullable = false)
    private EstadoPago estado;

    @Column(name = "FECHA_PAGO", nullable = false)
    private LocalDateTime fechaPago;

    @Column(name = "MONTO", precision = 10, scale = 2, nullable = false)
    private BigDecimal monto;

    @Column(name = "MONEDA", length = 3, nullable = false)
    private String moneda;

    /** Id del cargo en la pasarela (en Culqi empieza con "chr_"). */
    @Column(name = "REFERENCIA_EXTERNA", length = 100)
    private String referenciaExterna;

    /** Mensaje de la pasarela, por ejemplo el motivo de un rechazo. */
    @Column(name = "MENSAJE", length = 255)
    private String mensaje;
}
