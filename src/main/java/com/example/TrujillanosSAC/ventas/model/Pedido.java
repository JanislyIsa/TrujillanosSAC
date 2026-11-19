package com.example.TrujillanosSAC.ventas.model;

import com.example.TrujillanosSAC.usuarios.model.Cliente;
import com.example.TrujillanosSAC.ventas.model.codigo.EstadoPedidoCodigo;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity(name = "VentaPedido")
@Table(name = "PEDIDOS")
@Getter
@Setter
@NoArgsConstructor
public class Pedido {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID_PEDIDO")
    private Long idPedido;

    /** La genera el backend al guardar (@PrePersist); no se recibe en el request ni se modifica después. */
    @Column(name = "FECHA_REGISTRO", nullable = false, updatable = false)
    private LocalDateTime fechaRegistro;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "CLIENTE", nullable = false)
    private Cliente cliente;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "ESTADO", nullable = false)
    private EstadoPedido estado;

    @Column(name = "TOTAL", precision = 10, scale = 2, nullable = false)
    private BigDecimal total;

    @OneToMany(mappedBy = "pedido", cascade = CascadeType.ALL, orphanRemoval = true)
    @OrderBy("idDetalle")
    private List<DetallePedido> detalles = new ArrayList<>();

    @PrePersist
    public void prePersist() {
        fechaRegistro = LocalDateTime.now();
        // El estado inicial (PENDIENTE) lo asigna el service: es un registro de catálogo
        // (tabla ESTADO_PEDIDO), no un enum, así que no se puede crear aquí sin el repositorio.
    }

    public void agregarDetalle(DetallePedido detalle) {
        detalle.setPedido(this);
        detalles.add(detalle);
    }

    public boolean estaEn(EstadoPedidoCodigo codigo) {
        return estado != null && codigo.name().equals(estado.getCodigo());
    }
}
