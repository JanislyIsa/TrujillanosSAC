package com.example.TrujillanosSAC.usuarios.repository;

import com.example.TrujillanosSAC.usuarios.model.TipoDocumento;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface TipoDocumentoRepository extends JpaRepository<TipoDocumento, Long> {
    Optional<TipoDocumento> findByAbreviaturaIgnoreCase(String abreviatura);
    boolean existsByAbreviaturaIgnoreCase(String abreviatura);
    boolean existsByAbreviaturaIgnoreCaseAndIdDocumentoNot(String abreviatura, Long idDocumento);
    boolean existsByNombreIgnoreCase(String nombre);
    boolean existsByNombreIgnoreCaseAndIdDocumentoNot(String nombre, Long idDocumento);
}
