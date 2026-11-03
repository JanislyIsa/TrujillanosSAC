package com.example.TrujillanosSAC.Repository;

import com.example.TrujillanosSAC.Model.MetodoPago;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface MetodoPagoRepository extends JpaRepository<MetodoPago, Long> {
    Optional<MetodoPago> findByNombreIgnoreCase(String nombre);
}
