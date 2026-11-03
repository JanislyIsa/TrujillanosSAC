package com.example.TrujillanosSAC.Model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "ESTADO_PEDIDO")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EstadoPedido {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID_ESTADO")
    private  Long idEstado;

    @NotBlank
    @Column(name = "NOMBRE", length = 100, nullable = false)
    private String nombre;
}
