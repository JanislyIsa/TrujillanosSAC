package com.example.TrujillanosSAC.Repository;

import com.example.TrujillanosSAC.Model.ComprobanteCompra;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ComprobanteCompraRepository extends JpaRepository<ComprobanteCompra, Long> {
    boolean existsByOrdenCompra_IdOrdenCompra(Long idOrdenCompra);
}
