package com.example.TrujillanosSAC.reportes.repository;

import com.example.TrujillanosSAC.compras.model.OrdenCompra;
import com.example.TrujillanosSAC.reportes.dto.OrdenesPorProveedorReporteDTO;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.Repository;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;

public interface ReporteComprasRepository extends Repository<OrdenCompra, Long> {

    @Query("""
            select new com.example.TrujillanosSAC.reportes.dto.OrdenesPorProveedorReporteDTO(
                proveedor.idPersona,
                concat(concat(persona.nombre, ' '), persona.apellido),
                count(distinct orden.idOrdenCompra),
                sum(detalle.subtotal)
            )
            from ComprasOrdenCompra orden
            join orden.cotizacion cotizacion
            join cotizacion.proveedor proveedor
            join proveedor.persona persona
            join orden.detalles detalle
            where (:desde is null or orden.fechaEmision >= :desde)
              and (:hasta is null or orden.fechaEmision <= :hasta)
            group by proveedor.idPersona, persona.nombre, persona.apellido
            order by sum(detalle.subtotal) desc
            """)
    List<OrdenesPorProveedorReporteDTO> ordenesPorProveedor(@Param("desde") LocalDate desde,
                                                            @Param("hasta") LocalDate hasta);
}
