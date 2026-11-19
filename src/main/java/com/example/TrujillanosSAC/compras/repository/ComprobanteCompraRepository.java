package com.example.TrujillanosSAC.compras.repository;

import com.example.TrujillanosSAC.compras.model.ComprobanteCompra;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import jakarta.persistence.LockModeType;
import java.util.List;
import java.util.Optional;
import java.util.Collection;

@org.springframework.stereotype.Repository("comprasComprobanteCompraRepository")
public interface ComprobanteCompraRepository extends JpaRepository<ComprobanteCompra, Long> {
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select c from ComprasComprobanteCompra c where c.idComprobante = :id")
    Optional<ComprobanteCompra> findByIdForUpdate(@Param("id") Long id);

    List<ComprobanteCompra> findByOrdenCompraIdOrdenCompra(Long ordenCompraId);
    boolean existsByOrdenCompraIdOrdenCompra(Long ordenCompraId);
    Optional<ComprobanteCompra> findFirstByOrdenCompraIdOrdenCompraOrderByFechaEmisionDesc(Long ordenCompraId);
    List<ComprobanteCompra> findByOrdenCompraIdOrdenCompraIn(Collection<Long> ordenCompraIds);
}
