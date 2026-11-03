package com.example.TrujillanosSAC.Model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "METODO_PAGO")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MetodoPago {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID_METODO")
    private Long idMetodo;

    @NotBlank
    @Column(name = "NOMBRE", length = 50, nullable = false)
    private String nombre;
}
