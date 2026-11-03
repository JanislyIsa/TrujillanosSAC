package com.example.TrujillanosSAC.inventario.model;

import com.example.TrujillanosSAC.Model.Empleado;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "ALMACEN")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Almacen {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID_ALMACEN")
    private Long idAlmacen;

    @NotBlank
    @Column(name = "NOMBRE", length = 100, nullable = false)
    private String nombre;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ID_PERSONA", nullable = false)
    private Empleado empleadoResponsable;
}
