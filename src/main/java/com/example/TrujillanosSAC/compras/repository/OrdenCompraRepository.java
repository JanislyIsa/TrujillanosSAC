package com.example.TrujillanosSAC.compras.repository;

import com.example.TrujillanosSAC.compras.model.OrdenCompra;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;
import java.util.List;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

@org.springframework.stereotype.Repository("comprasOrdenCompraRepository")
public interface OrdenCompraRepository extends JpaRepository<OrdenCompra, Long> {
    Optional<OrdenCompra> findByCotizacionIdCotizacion(Long cotizacionId);
    @Query("""
            select distinct o from ComprasOrdenCompra o
            join fetch o.cotizacion c join fetch c.proveedor p join fetch p.persona
            left join fetch o.detalles d left join fetch d.detalleCotizacion dc
            left join fetch dc.insumo i left join fetch i.unidadMedida
            where p.idPersona = :proveedorId order by o.fechaEmision desc
            """)
    List<OrdenCompra> findAllByProveedor(@Param("proveedorId") Long proveedorId);
}
