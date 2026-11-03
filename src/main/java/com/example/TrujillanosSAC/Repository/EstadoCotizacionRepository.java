package com.example.TrujillanosSAC.Repository;

import com.example.TrujillanosSAC.Model.EstadoCotizacion;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface EstadoCotizacionRepository extends JpaRepository<EstadoCotizacion, Long> {
    Optional<EstadoCotizacion> findFirstByOrderByIdEstadoCotizacionAsc();

    Optional<EstadoCotizacion> findByNombreIgnoreCase(String nombre);
}
