package com.example.TrujillanosSAC.compras.model;

import com.example.TrujillanosSAC.Model.Proveedor;
import com.example.TrujillanosSAC.inventario.model.SolicitudCompra;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "COTIZACION")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Cotizacion {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID_COTIZACION")
    private Long idCotizacion;

    @NotNull @Column(name = "FECHA_COT", nullable = false)
    private LocalDate fechaCot;

    @NotNull @Column(name = "FECHA_ENTREGA", nullable = false)
    private LocalDate fechaEntrega;

    @NotNull @Column(name = "MONTO", precision = 12, scale = 2, nullable = false)
    private BigDecimal monto;

    @NotNull @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "ID_ESTADO", nullable = false)
    private EstadoCotizacion estadoCotizacion;

    @NotNull @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "ID_PROVEEDOR", nullable = false)
    private Proveedor proveedor;

    @NotNull @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "ID_SOLICITUD", nullable = false)
    private SolicitudCompra solicitudCompra;

    @Builder.Default
    @OneToMany(mappedBy = "cotizacion", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<DetalleCotizacion> detalles = new ArrayList<>();

    public void agregarDetalle(DetalleCotizacion detalle) {
        detalles.add(detalle);
        detalle.setCotizacion(this);
    }
}
