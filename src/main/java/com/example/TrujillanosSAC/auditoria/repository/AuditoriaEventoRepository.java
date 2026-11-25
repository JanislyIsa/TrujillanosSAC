package com.example.TrujillanosSAC.auditoria.repository;

import com.example.TrujillanosSAC.auditoria.model.AuditoriaEvento;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.time.LocalDateTime;

public interface AuditoriaEventoRepository extends JpaRepository<AuditoriaEvento, Long> {
    @Query("""
            select e from AuditoriaEvento e
            where (:usuario is null or upper(e.usuario) = upper(:usuario))
              and (:accion is null or e.accion = :accion)
              and (:desde is null or e.fecha >= :desde)
              and (:hasta is null or e.fecha < :hasta)
            """)
    Page<AuditoriaEvento> buscar(@Param("usuario") String usuario, @Param("accion") String accion,
                                 @Param("desde") LocalDateTime desde, @Param("hasta") LocalDateTime hasta,
                                 Pageable pageable);
}
