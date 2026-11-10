package com.example.TrujillanosSAC.compras.repository;

import com.example.TrujillanosSAC.compras.model.EstadoCotizacion;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface EstadoCotizacionRepository extends JpaRepository<EstadoCotizacion, Long> {
    Optional<EstadoCotizacion> findByNombreIgnoreCase(String nombre);
}
