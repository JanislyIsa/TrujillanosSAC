package com.example.TrujillanosSAC.Repository;

import com.example.TrujillanosSAC.Model.OrdenCompra;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface OrdenCompraRepository extends JpaRepository<OrdenCompra, Long> {
    @Query("""
            select o
            from OrdenCompra o
            join fetch o.cotizacion c
            left join fetch c.estadoCotizacion
            left join fetch c.proveedor p
            left join fetch p.persona
            order by o.idOrdenCompra desc
            """)
    List<OrdenCompra> findAllParaAdmin();

    @Query("""
            select o
            from OrdenCompra o
            join fetch o.cotizacion c
            left join fetch c.estadoCotizacion
            where c.idCotizacion = :idCotizacion
            """)
    Optional<OrdenCompra> findFirstByCotizacionParaProveedor(@Param("idCotizacion") Long idCotizacion);

    boolean existsByCotizacion_SolicitudCompra_IdSolicitud(Long idSolicitud);
}
