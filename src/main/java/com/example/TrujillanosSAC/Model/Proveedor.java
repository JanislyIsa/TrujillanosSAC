package com.example.TrujillanosSAC.Model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "PROVEEDOR")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Proveedor {
    @Id
    @Column(name = "ID_PERSONA")
    private Long idPersona;

    @NotNull
    @OneToOne(fetch = FetchType.LAZY)
    @MapsId
    @JoinColumn(name = "ID_PERSONA")
    private Persona persona;

    @NotBlank
    @Email
    @Column(name = "CORREO", length = 100, nullable = false, unique = true)
    private String correo;

    @NotBlank
    @Column(name = "DIRECCION", length = 100, nullable = false)
    private String direccion;
}
