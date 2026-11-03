package com.example.TrujillanosSAC.inventario.repository;

import com.example.TrujillanosSAC.inventario.model.DetalleSolicitud;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface DetalleSolicitudRepository extends JpaRepository<DetalleSolicitud, Long> {
    @Query("""
            select d
            from DetalleSolicitud d
            join fetch d.insumo i
            left join fetch i.unidadMedida
            where d.solicitudCompra.idSolicitud = :idSolicitud
            order by d.idDetalleSol
            """)
    List<DetalleSolicitud> findBySolicitudParaProveedor(@Param("idSolicitud") Long idSolicitud);
}
