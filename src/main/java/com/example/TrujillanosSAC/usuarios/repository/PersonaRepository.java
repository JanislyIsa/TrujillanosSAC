package com.example.TrujillanosSAC.usuarios.repository;

import com.example.TrujillanosSAC.usuarios.model.Persona;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PersonaRepository extends JpaRepository<Persona, Long> {
    Optional<Persona> findByNumDoc(String numDoc);
    boolean existsByNumDoc(String numDoc);
    boolean existsByNumDocAndIdPersonaNot(String numDoc, Long idPersona);
    boolean existsByDocumentoIdDocumento(Long documentoId);
}
