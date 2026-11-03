package com.example.TrujillanosSAC.Model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "EMPLEADOS")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Empleado {
    @Id
    @Column(name = "ID_PERSONA")
    private Long idPersona;

    @NotNull
    @OneToOne(fetch = FetchType.LAZY)
    @MapsId
    @JoinColumn(name = "ID_PERSONA")
    private Persona persona;

    @NotNull
    @Column(name = "SALARIO", precision = 10, scale = 2, nullable = false)
    private BigDecimal salario;

    @NotNull
    @Column(name = "FECHA_CONTRATACION", nullable = false)
    private LocalDate fechaContratacion;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "CARGO", nullable = false)
    private Cargo cargo;
}
