package com.example.TrujillanosSAC.ventas.repository;

import com.example.TrujillanosSAC.ventas.model.MetodoPago;
import org.springframework.stereotype.Repository;

@Repository("ventasMetodoPagoRepository")
public interface MetodoPagoRepository extends CatalogoRepository<MetodoPago> {
}
