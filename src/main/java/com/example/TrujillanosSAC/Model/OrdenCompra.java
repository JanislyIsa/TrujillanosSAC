package com.example.TrujillanosSAC.Model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Entity
@Table(name = "ORDEN_COMPRA")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrdenCompra {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID_ORDEN_COMPRA")
    private Long idOrdenCompra;

    @NotNull
    @Column(name = "FECHA_EMISION", nullable = false)
    private LocalDate fechaEmision;

    @Size(max = 300)
    @Column(name = "DESCRIPCION", length = 300)
    private String descripcion;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ID_COTIZACION", nullable = false)
    private Cotizacion cotizacion;
}
