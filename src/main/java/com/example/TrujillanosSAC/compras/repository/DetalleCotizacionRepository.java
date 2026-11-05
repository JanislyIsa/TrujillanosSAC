package com.example.TrujillanosSAC.compras.repository;

import com.example.TrujillanosSAC.compras.model.DetalleCotizacion;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface DetalleCotizacionRepository extends JpaRepository<DetalleCotizacion, Long> {
    List<DetalleCotizacion> findByCotizacionIdCotizacion(Long cotizacionId);
}
