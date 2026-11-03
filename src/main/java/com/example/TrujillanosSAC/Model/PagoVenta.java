package com.example.TrujillanosSAC.Model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "PAGO_VENTA")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PagoVenta {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID_PAGO")
    private Long idPago;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ID_COMPROBANTE", nullable = false)
    private ComprobanteVenta comprobante;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ID_METODO", nullable = false)
    private MetodoPago metodoPago;

    @NotNull
    @Column(name = "FECHA_PAGO", nullable = false)
    private LocalDateTime fechaPago;

    @NotNull
    @Column(name = "TOTAL", precision = 10, scale = 2, nullable = false)
    private BigDecimal total;
}
