package com.example.TrujillanosSAC.Repository;

import com.example.TrujillanosSAC.Model.DetallePedido;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Collection;
import java.util.List;

public interface DetallePedidoRepository extends JpaRepository<DetallePedido, Long> {
    @Query("""
            select d
            from DetallePedido d
            join fetch d.pedido
            join fetch d.producto p
            left join fetch p.presentacion
            where d.pedido.idPedido in :pedidoIds
            order by d.idDetalle
            """)
    List<DetallePedido> findByPedidoIds(@Param("pedidoIds") Collection<Long> pedidoIds);

    @Query("""
            select d
            from DetallePedido d
            join fetch d.producto
            where d.pedido.idPedido = :pedidoId
            order by d.idDetalle
            """)
    List<DetallePedido> findByPedidoIdConProducto(@Param("pedidoId") Long pedidoId);
}
