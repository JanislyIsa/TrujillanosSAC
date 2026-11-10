package com.example.TrujillanosSAC.compras.repository;

import com.example.TrujillanosSAC.compras.model.EstadoComprobanteCompra;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

@org.springframework.stereotype.Repository("comprasEstadoComprobanteCompraRepository")
public interface EstadoComprobanteCompraRepository extends JpaRepository<EstadoComprobanteCompra, Long> {
    Optional<EstadoComprobanteCompra> findByNombreIgnoreCase(String nombre);
}
