package com.example.TrujillanosSAC.inventario.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "ESTADO_SOLICITUD")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EstadoSolicitudCompra {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID_ESTADO")
    private Long idEstadoSolicitudCompra;

    @NotBlank
    @Column(name = "NOMBRE", length = 50, nullable = false, unique = true)
    private String nombre;
}
