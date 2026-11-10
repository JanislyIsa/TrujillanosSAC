package com.example.TrujillanosSAC.Model;

import com.example.TrujillanosSAC.inventario.model.SolicitudCompra;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import com.example.TrujillanosSAC.usuarios.model.*;

@Entity
@Table(name = "COTIZACION")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Cotizacion {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID_COTIZACION")
    private Long idCotizacion;

    @NotNull
    @Column(name = "FECHA_COT", nullable = false)
    private LocalDate fechaCot;

    @NotNull
    @Column(name = "MONTO", precision = 12, scale = 2, nullable = false)
    private BigDecimal monto;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ID_ESTADO", nullable = false)
    private EstadoCotizacion estadoCotizacion;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ID_PROVEEDOR", nullable = false)
    private Proveedor proveedor;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ID_SOLICITUD", nullable = false)
    private SolicitudCompra solicitudCompra;

    @NotNull
    @Column(name = "FECHA_ENTREGA", nullable = false)
    private LocalDate fechaEntrega;
}
