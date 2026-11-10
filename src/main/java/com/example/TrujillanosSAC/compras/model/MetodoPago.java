package com.example.TrujillanosSAC.compras.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Entity(name = "ComprasMetodoPago")
@Table(name = "METODO_PAGO")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class MetodoPago {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID_METODO")
    private Long idMetodo;

    @NotBlank
    @Column(name = "NOMBRE", length = 50, nullable = false)
    private String nombre;
}
