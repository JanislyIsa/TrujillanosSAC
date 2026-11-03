package com.example.TrujillanosSAC.inventario.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "INSUMO")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Insumo {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID_INSUMO")
    private Long idInsumo;

    @Builder.Default
    @Convert(converter = org.hibernate.type.NumericBooleanConverter.class)
    @Column(name = "ACTIVO", nullable = false)
    private Boolean activo = true;

    @NotBlank
    @jakarta.validation.constraints.Size(max = 100)
    @Column(name = "NOMBRE", length = 100, nullable = false)
    private String nombre;

    @NotNull
    @jakarta.validation.constraints.PositiveOrZero
    @Column(name = "STOCK", nullable = false)
    private Integer stock;

    @NotNull
    @Column(name = "STOCK_MINIMO", nullable = false)
    private Integer stockMinimo;

    @NotNull
    @Column(name = "STOCK_LIMITE", nullable = false)
    private Integer stockLimite;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ID_ALMACEN", nullable = false)
    private Almacen almacen;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ID_UNIDAD_MEDIDA", nullable = false)
    private UnidadMedida unidadMedida;
}
