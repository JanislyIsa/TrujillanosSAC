package com.example.TrujillanosSAC.usuarios.repository;

import com.example.TrujillanosSAC.usuarios.model.Empleado;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface EmpleadoRepository extends JpaRepository<Empleado, Long> {
    @Query("select e from Empleado e join fetch e.cargo")
    java.util.List<Empleado> findAllConCargo();
    @Query("select e from Empleado e join fetch e.cargo where e.idPersona = :idPersona")
    Optional<Empleado> findByIdPersonaConCargo(@Param("idPersona") Long idPersona);
    boolean existsByCargoIdCargo(Long cargoId);
}
