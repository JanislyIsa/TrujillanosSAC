package com.example.TrujillanosSAC.Repository;

import com.example.TrujillanosSAC.Model.DetalleCotizacion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface DetalleCotizacionRepository extends JpaRepository<DetalleCotizacion, Long> {
    @Query("""
            select d
            from DetalleCotizacion d
            join fetch d.insumo i
            left join fetch i.unidadMedida
            where d.cotizacion.idCotizacion = :idCotizacion
            order by d.idDetalleCot
            """)
    List<DetalleCotizacion> findByCotizacionParaProveedor(@Param("idCotizacion") Long idCotizacion);
}
