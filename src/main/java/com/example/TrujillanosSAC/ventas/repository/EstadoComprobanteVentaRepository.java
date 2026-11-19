package com.example.TrujillanosSAC.ventas.repository;

import com.example.TrujillanosSAC.ventas.model.EstadoComprobanteVenta;
import org.springframework.stereotype.Repository;

@Repository("ventasEstadoComprobanteVentaRepository")
public interface EstadoComprobanteVentaRepository extends CatalogoRepository<EstadoComprobanteVenta> {
}
