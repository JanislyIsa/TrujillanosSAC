package com.example.TrujillanosSAC.Repository;

import com.example.TrujillanosSAC.Model.DetalleOrdenCompra;
import com.example.TrujillanosSAC.Dto.ReporteComprasProveedorDto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface DetalleOrdenCompraRepository extends JpaRepository<DetalleOrdenCompra, Long> {
    @Query("""
            select d
            from DetalleOrdenCompra d
            join fetch d.detalleCotizacion dc
            join fetch dc.insumo i
            left join fetch i.unidadMedida
            where d.ordenCompra.idOrdenCompra = :idOrden
            order by d.idDetalleOrden
            """)
    List<DetalleOrdenCompra> findByOrdenParaAdmin(@Param("idOrden") Long idOrden);

    @Query("""
            select new com.example.TrujillanosSAC.Dto.ReporteComprasProveedorDto(
                concat(concat(pe.nombre, ' '), pe.apellido),
                count(distinct o.idOrdenCompra),
                sum(d.subtotal),
                min(d.precioUnitario),
                max(d.precioUnitario)
            )
            from DetalleOrdenCompra d
            join d.ordenCompra o
            join o.cotizacion c
            join c.proveedor prov
            join prov.persona pe
            group by pe.nombre, pe.apellido
            order by sum(d.subtotal) desc
            """)
    List<ReporteComprasProveedorDto> reporteComprasPorProveedor();
}
