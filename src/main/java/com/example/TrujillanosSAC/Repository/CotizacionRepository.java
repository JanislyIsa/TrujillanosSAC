package com.example.TrujillanosSAC.Repository;

import com.example.TrujillanosSAC.Model.Cotizacion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface CotizacionRepository extends JpaRepository<Cotizacion, Long> {
    @Query("""
            select c
            from Cotizacion c
            left join fetch c.estadoCotizacion
            left join fetch c.proveedor p
            left join fetch p.persona
            left join fetch c.solicitudCompra
            where c.proveedor.idPersona = :idProveedor
            order by c.idCotizacion desc
            """)
    List<Cotizacion> findByProveedorParaPanel(@Param("idProveedor") Long idProveedor);

    @Query("""
            select c
            from Cotizacion c
            left join fetch c.estadoCotizacion
            left join fetch c.proveedor p
            left join fetch p.persona
            left join fetch c.solicitudCompra s
            order by c.idCotizacion desc
            """)
    List<Cotizacion> findAllParaAdmin();
}
