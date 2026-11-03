package com.example.TrujillanosSAC.Model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
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
}
