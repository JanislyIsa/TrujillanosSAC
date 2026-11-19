package com.example.TrujillanosSAC.ventas.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity(name = "VentaEstadoPedido")
@Table(name = "ESTADO_PEDIDO")
@Getter
@Setter
@NoArgsConstructor
public class EstadoPedido extends Catalogo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID_ESTADO")
    private Long id;
}
