package com.example.TrujillanosSAC.compras.repository;

import com.example.TrujillanosSAC.compras.model.PagoCompra;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Collection;

@org.springframework.stereotype.Repository("comprasPagoCompraRepository")
public interface PagoCompraRepository extends JpaRepository<PagoCompra, Long> {
    List<PagoCompra> findByComprobanteCompraIdComprobante(Long comprobanteId);
    List<PagoCompra> findByComprobanteCompraIdComprobanteIn(Collection<Long> comprobanteIds);
}
