package com.example.TrujillanosSAC.inventario.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "UNIDAD_MEDIDA")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UnidadMedida {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID_UNIDAD_MEDIDA")
    private Long idUnidadMedida;

    @NotBlank
    @Column(name = "NOMBRE", length = 100)
    private String nombre;
}
