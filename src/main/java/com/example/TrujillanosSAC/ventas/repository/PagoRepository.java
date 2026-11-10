package com.example.TrujillanosSAC.ventas.repository;

import com.example.TrujillanosSAC.ventas.model.Pago;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository("ventasPagoRepository")
public interface PagoRepository extends JpaRepository<Pago, Long> {

    @Query("select p from VentaPago p join fetch p.metodoPago join fetch p.estado order by p.idPago desc")
    List<Pago> listarTodos();

    @Query("""
            select p from VentaPago p join fetch p.metodoPago join fetch p.estado
            where p.pedido.idPedido = :pedidoId order by p.idPago desc
            """)
    List<Pago> listarPorPedido(@Param("pedidoId") Long pedidoId);

    @Query("""
            select p from VentaPago p join fetch p.metodoPago join fetch p.estado
            where p.pedido.cliente.idPersona = :clienteId order by p.idPago desc
            """)
    List<Pago> listarPorCliente(@Param("clienteId") Long clienteId);
}
