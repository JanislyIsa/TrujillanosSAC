package com.example.TrujillanosSAC.usuarios.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Entity
@Table(name = "CLIENTES")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Cliente {
    @Id
    @Column(name = "ID_PERSONA")
    private Long idPersona;

    @NotNull
    @OneToOne(fetch = FetchType.LAZY)
    @MapsId
    @JoinColumn(name = "ID_PERSONA")
    private Persona persona;

    @NotNull
    @Column(name = "FECHA_REGISTRO", nullable = false)
    private LocalDate fechaRegistro;

    /** Correo del cliente. Opcional: los clientes creados por un empleado pueden no tenerlo. */
    @Size(max = 100)
    @Column(name = "CORREO", length = 100)
    private String correo;
}
