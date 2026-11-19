package com.example.TrujillanosSAC.usuarios.repository;

import com.example.TrujillanosSAC.usuarios.model.Proveedor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface ProveedorRepository extends JpaRepository<Proveedor, Long> {
    Optional<Proveedor> findFirstByOrderByIdPersonaAsc();

    Optional<Proveedor> findByIdPersona(Long idPersona);
    boolean existsByCorreoIgnoreCase(String correo);
    boolean existsByCorreoIgnoreCaseAndIdPersonaNot(String correo, Long idPersona);

    @Query("""
            select p
            from Proveedor p
            join fetch p.persona
            order by p.persona.nombre, p.persona.apellido
            """)
    List<Proveedor> findAllConPersona();
}
