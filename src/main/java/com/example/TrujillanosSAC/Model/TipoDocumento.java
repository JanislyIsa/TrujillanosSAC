package com.example.TrujillanosSAC.Model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "TIPO_DOCUMENTO")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TipoDocumento {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID_DOCUMENTO")
    private Long idDocumento;

    @NotBlank
    @Column(name = "NOMBRE", length = 20, nullable = false, unique = true)
    private String nombre;

    @NotBlank
    @Column(name = "ABREVIATURA", length = 10, nullable = false, unique = true)
    private String abreviatura;

    @NotNull
    @Column(name = "LONGITUD", nullable = false)
    private Integer longitud;
}
