package com.example.TrujillanosSAC.compras.repository;

import com.example.TrujillanosSAC.compras.model.OrdenCompra;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

@org.springframework.stereotype.Repository("comprasOrdenCompraRepository")
public interface OrdenCompraRepository extends JpaRepository<OrdenCompra, Long> {
    Optional<OrdenCompra> findByCotizacionIdCotizacion(Long cotizacionId);
}
