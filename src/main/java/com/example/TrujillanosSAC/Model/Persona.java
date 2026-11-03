package com.example.TrujillanosSAC.Model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "PERSONA")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Persona {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID_PERSONA")
    private Long idPersona;

    @NotBlank
    @Size(max = 100)
    @Column(name = "NOMBRE", length = 100, nullable = false)
    private String nombre;

    @NotBlank
    @Size(max = 100)
    @Column(name = "APELLIDO", length = 100, nullable = false)
    private String apellido;

    @NotBlank
    @Pattern(regexp = "\\d{9}", message = "El telefono debe tener 9 digitos")
    @Column(name = "TELEFONO", length = 9, nullable = false)
    private String telefono;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "DOCUMENTO", nullable = false)
    private TipoDocumento documento;

    @NotBlank
    @Size(max = 20)
    @Column(name = "NUM_DOC", length = 20, nullable = false)
    private String numDoc;
}
