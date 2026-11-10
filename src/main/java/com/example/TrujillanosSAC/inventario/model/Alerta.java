package com.example.TrujillanosSAC.inventario.model;

import com.example.TrujillanosSAC.usuarios.model.Empleado;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Entity
@Table(name = "ALERTA")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Alerta {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID_ALERTA")
    private Long idAlerta;

    @Column(name = "TIPO_ITEM", length = 20)
    private String tipoItem;

    @Column(name = "ID_ITEM")
    private Long idItem;

    @Column(name = "FECHA_HORA")
    private java.time.LocalDateTime fechaHora;

    @Column(name = "RESUELTA_EN")
    private java.time.LocalDateTime resueltaEn;

    @Column(name = "STOCK_ACTUAL")
    private Integer stockActual;

    @Column(name = "STOCK_MINIMO")
    private Integer stockMinimo;

    @Column(name = "STOCK_LIMITE")
    private Integer stockLimite;

    @NotBlank
    @Column(name = "TIPO_ALERTA", length = 20, nullable = false)
    private String tipoAlerta;

    @NotBlank
    @Column(name = "DESCRIPCION", length = 200, nullable = false)
    private String descripcion;

    @NotNull
    @Column(name = "FECHA", nullable = false)
    private LocalDate fecha;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ID_ALMACEN", nullable = false)
    private Almacen almacen;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ID_PERSONA", nullable = false)
    private Empleado empleado;
}
