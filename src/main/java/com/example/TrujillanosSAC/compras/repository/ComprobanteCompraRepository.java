package com.example.TrujillanosSAC.compras.repository;

import com.example.TrujillanosSAC.compras.model.ComprobanteCompra;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

@org.springframework.stereotype.Repository("comprasComprobanteCompraRepository")
public interface ComprobanteCompraRepository extends JpaRepository<ComprobanteCompra, Long> {
    List<ComprobanteCompra> findByOrdenCompraIdOrdenCompra(Long ordenCompraId);
    boolean existsByOrdenCompraIdOrdenCompra(Long ordenCompraId);
    Optional<ComprobanteCompra> findFirstByOrdenCompraIdOrdenCompraOrderByFechaEmisionDesc(Long ordenCompraId);
}
