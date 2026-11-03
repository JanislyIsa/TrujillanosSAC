package com.example.TrujillanosSAC.Repository;

import com.example.TrujillanosSAC.Model.TipoDocumento;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface TipoDocumentoRepository extends JpaRepository<TipoDocumento, Long> {
    Optional<TipoDocumento> findByAbreviaturaIgnoreCase(String abreviatura);
}
