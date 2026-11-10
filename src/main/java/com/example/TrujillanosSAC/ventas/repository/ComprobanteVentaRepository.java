package com.example.TrujillanosSAC.ventas.repository;

import com.example.TrujillanosSAC.ventas.model.ComprobanteVenta;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository("ventasComprobanteVentaRepository")
public interface ComprobanteVentaRepository extends JpaRepository<ComprobanteVenta, Long> {

    boolean existsByPedido_IdPedido(Long pedidoId);

    // NUMERO tiene ceros a la izquierda ("00000012"), así que ordenarlo como texto da el mismo orden que como número.
    Optional<ComprobanteVenta> findTopBySerieOrderByNumeroDesc(String serie);

    @Query("select c from VentaComprobanteVenta c join fetch c.tipo join fetch c.estado order by c.idComprobante desc")
    List<ComprobanteVenta> listarTodos();
}
