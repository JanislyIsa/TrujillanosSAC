package com.example.TrujillanosSAC.compras.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "PAGO_COMPRA")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class PagoCompra {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID_PAGO")
    private Long idPago;

    @NotNull @Positive @Column(name = "MONTO", precision = 12, scale = 2, nullable = false)
    private BigDecimal monto;

    @NotNull @Column(name = "FECHA_PAGO", nullable = false)
    private LocalDate fechaPago;

    @NotNull @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "ID_COMPROBANTE", nullable = false)
    private ComprobanteCompra comprobanteCompra;

    @NotNull @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "ID_METODO_PAGO", nullable = false)
    private MetodoPago metodoPago;
}
