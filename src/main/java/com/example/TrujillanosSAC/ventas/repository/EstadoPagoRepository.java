package com.example.TrujillanosSAC.ventas.repository;

import com.example.TrujillanosSAC.ventas.model.EstadoPago;
import org.springframework.stereotype.Repository;

@Repository("ventasEstadoPagoRepository")
public interface EstadoPagoRepository extends CatalogoRepository<EstadoPago> {
}
