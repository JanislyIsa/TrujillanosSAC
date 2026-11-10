package com.example.TrujillanosSAC.usuarios.repository;

import com.example.TrujillanosSAC.usuarios.model.Rol;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RolRepository extends JpaRepository<Rol, Long> {
    Optional<Rol> findByNombreIgnoreCase(String nombre);
    boolean existsByNombreIgnoreCase(String nombre);
    boolean existsByNombreIgnoreCaseAndIdRolNot(String nombre, Long idRol);
}
