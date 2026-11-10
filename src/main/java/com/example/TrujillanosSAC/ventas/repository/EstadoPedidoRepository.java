package com.example.TrujillanosSAC.ventas.repository;

import com.example.TrujillanosSAC.ventas.model.EstadoPedido;
import org.springframework.stereotype.Repository;

@Repository("ventasEstadoPedidoRepository")
public interface EstadoPedidoRepository extends CatalogoRepository<EstadoPedido> {
}
