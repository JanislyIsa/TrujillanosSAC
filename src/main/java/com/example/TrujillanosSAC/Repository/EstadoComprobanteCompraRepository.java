package com.example.TrujillanosSAC.Repository;

import com.example.TrujillanosSAC.Model.EstadoComprobanteCompra;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface EstadoComprobanteCompraRepository extends JpaRepository<EstadoComprobanteCompra, Long> {
    Optional<EstadoComprobanteCompra> findByNombreIgnoreCase(String nombre);
}
