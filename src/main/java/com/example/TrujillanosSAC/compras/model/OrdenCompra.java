package com.example.TrujillanosSAC.compras.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "ORDEN_COMPRA")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class OrdenCompra {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID_ORDEN_COMPRA")
    private Long idOrdenCompra;

    @NotNull @Column(name = "FECHA_EMISION", nullable = false)
    private LocalDate fechaEmision;

    @Size(max = 300) @Column(name = "DESCRIPCION", length = 300)
    private String descripcion;

    @NotNull @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "ID_COTIZACION", nullable = false)
    private Cotizacion cotizacion;

    @Builder.Default
    @OneToMany(mappedBy = "ordenCompra", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<DetalleOrdenCompra> detalles = new ArrayList<>();

    public void agregarDetalle(DetalleOrdenCompra detalle) {
        detalles.add(detalle);
        detalle.setOrdenCompra(this);
    }
}
