package com.example.TrujillanosSAC.inventario.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "DETALLE_MOV_INSUMO")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DetalleMovInsumo {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID_DETALLE_INSUMO")
    private Long idDetalleInsumo;

    @Column(name = "STOCK_ANTERIOR")
    private Integer stockAnterior;

    @Column(name = "STOCK_NUEVO")
    private Integer stockNuevo;

    @Column(name = "NOMBRE_ITEM", length = 100)
    private String nombreItem;

    @Column(name = "UNIDAD_ITEM", length = 100)
    private String unidadItem;

    @NotNull
    @jakarta.validation.constraints.Positive
    @Column(name = "CANTIDAD", nullable = false)
    private Integer cantidad;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ID_MOVIMIENTO", nullable = false)
    private Movimiento movimiento;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ID_INSUMO", nullable = false)
    private Insumo insumo;
}
