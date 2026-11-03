package com.example.TrujillanosSAC.inventario.repository;

import com.example.TrujillanosSAC.inventario.model.EstadoSolicitudCompra;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface EstadoSolicitudCompraRepository extends JpaRepository<EstadoSolicitudCompra, Long> {
    Optional<EstadoSolicitudCompra> findFirstByOrderByIdEstadoSolicitudCompraAsc();

    Optional<EstadoSolicitudCompra> findByNombreIgnoreCase(String nombre);
}
