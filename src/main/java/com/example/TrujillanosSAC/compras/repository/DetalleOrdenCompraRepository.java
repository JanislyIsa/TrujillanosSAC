package com.example.TrujillanosSAC.compras.repository;

import com.example.TrujillanosSAC.compras.model.DetalleOrdenCompra;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

@org.springframework.stereotype.Repository("comprasDetalleOrdenCompraRepository")
public interface DetalleOrdenCompraRepository extends JpaRepository<DetalleOrdenCompra, Long> {
    List<DetalleOrdenCompra> findByOrdenCompraIdOrdenCompra(Long ordenCompraId);
}
