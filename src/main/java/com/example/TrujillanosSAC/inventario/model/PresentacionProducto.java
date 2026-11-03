package com.example.TrujillanosSAC.inventario.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "PRESENTACION_PRODUCTO")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PresentacionProducto {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID_PRESENTACION")
    private Long idPresentacion;

    @NotBlank
    @Column(name = "NOMBRE", length = 50, nullable = false)
    private String nombre;
}
