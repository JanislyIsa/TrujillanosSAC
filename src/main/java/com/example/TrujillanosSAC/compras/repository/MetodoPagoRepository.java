package com.example.TrujillanosSAC.compras.repository;

import com.example.TrujillanosSAC.compras.model.MetodoPago;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

@org.springframework.stereotype.Repository("comprasMetodoPagoRepository")
public interface MetodoPagoRepository extends JpaRepository<MetodoPago, Long> {
    Optional<MetodoPago> findByNombreIgnoreCase(String nombre);
}
