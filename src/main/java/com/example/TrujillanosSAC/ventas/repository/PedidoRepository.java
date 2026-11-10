package com.example.TrujillanosSAC.ventas.repository;

import com.example.TrujillanosSAC.ventas.model.Pedido;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository("ventasPedidoRepository")
public interface PedidoRepository extends JpaRepository<Pedido, Long> {

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select p from VentaPedido p where p.idPedido = :id")
    Optional<Pedido> bloquear(@Param("id") Long id);

    // Los "join fetch" traen estado, detalles y productos en una sola consulta (evita el problema N+1).
    @Query("""
            select distinct p from VentaPedido p
            join fetch p.estado
            left join fetch p.detalles d
            left join fetch d.producto
            where p.idPedido = :id
            """)
    Optional<Pedido> buscarCompleto(@Param("id") Long id);

    @Query("""
            select distinct p from VentaPedido p
            join fetch p.estado
            left join fetch p.detalles d
            left join fetch d.producto
            order by p.fechaRegistro desc
            """)
    List<Pedido> listarCompletos();

    @Query("""
            select distinct p from VentaPedido p
            join fetch p.estado
            left join fetch p.detalles d
            left join fetch d.producto
            where p.cliente.idPersona = :clienteId
            order by p.fechaRegistro desc
            """)
    List<Pedido> listarCompletosPorCliente(@Param("clienteId") Long clienteId);

    @Query("select p.idPedido from VentaPedido p where p.estado.codigo = :codigo and p.fechaRegistro < :limite")
    List<Long> idsPorEstadoRegistradosAntesDe(@Param("codigo") String codigo, @Param("limite") LocalDateTime limite);
}
