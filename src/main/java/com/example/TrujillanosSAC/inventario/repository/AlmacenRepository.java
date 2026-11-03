package com.example.TrujillanosSAC.inventario.repository;

import com.example.TrujillanosSAC.inventario.model.Almacen;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface AlmacenRepository extends JpaRepository<Almacen, Long> {
    Optional<Almacen> findFirstByOrderByIdAlmacenAsc();
}
