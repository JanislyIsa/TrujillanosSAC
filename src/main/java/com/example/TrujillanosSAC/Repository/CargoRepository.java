package com.example.TrujillanosSAC.Repository;

import com.example.TrujillanosSAC.Model.Cargo;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CargoRepository extends JpaRepository<Cargo, Long> {
    Optional<Cargo> findByNombreIgnoreCase(String nombre);
}
