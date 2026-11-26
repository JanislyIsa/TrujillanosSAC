package com.example.TrujillanosSAC.compras.repository;

import com.example.TrujillanosSAC.compras.model.OrdenCompra;
import com.example.TrujillanosSAC.compras.dto.OrdenesPorProveedorReporteDTO;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;
import java.util.List;
import java.util.Collection;
import java.time.LocalDate;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

@org.springframework.stereotype.Repository("comprasOrdenCompraRepository")
public interface OrdenCompraRepository extends JpaRepository<OrdenCompra, Long> {
    Optional<OrdenCompra> findByCotizacionIdCotizacion(Long cotizacionId);
    List<OrdenCompra> findByCotizacionIdCotizacionIn(Collection<Long> cotizacionIds);
    Page<OrdenCompra> findByCotizacionProveedorIdPersona(Long proveedorId, Pageable pageable);
    @Query(value = """
            select o from ComprasOrdenCompra o
            join o.cotizacion c join c.proveedor p join p.persona persona
            where (:buscar = '' or lower(concat(concat(persona.nombre, ' '), persona.apellido)) like lower(concat('%', :buscar, '%'))
              or lower(concat('or_', function('TO_CHAR', o.idOrdenCompra))) like lower(concat('%', :buscar, '%'))
              or (lower(:buscar) like '%pagad%' and
                  (select coalesce(sum(pg.monto), 0) from ComprasPagoCompra pg where pg.comprobanteCompra.ordenCompra = o) >=
                  (select coalesce(sum(d.subtotal), 0) from ComprasDetalleOrdenCompra d where d.ordenCompra = o))
              or (lower(:buscar) like '%pendient%' and
                  (select coalesce(sum(pg.monto), 0) from ComprasPagoCompra pg where pg.comprobanteCompra.ordenCompra = o) <
                  (select coalesce(sum(d.subtotal), 0) from ComprasDetalleOrdenCompra d where d.ordenCompra = o))
              or (lower(:buscar) like '%esperando%' and not exists
                  (select cc.idComprobante from ComprasComprobanteCompra cc where cc.ordenCompra = o)))
            """,
            countQuery = """
            select count(o) from ComprasOrdenCompra o
            join o.cotizacion c join c.proveedor p join p.persona persona
            where (:buscar = '' or lower(concat(concat(persona.nombre, ' '), persona.apellido)) like lower(concat('%', :buscar, '%'))
              or lower(concat('or_', function('TO_CHAR', o.idOrdenCompra))) like lower(concat('%', :buscar, '%'))
              or (lower(:buscar) like '%pagad%' and
                  (select coalesce(sum(pg.monto), 0) from ComprasPagoCompra pg where pg.comprobanteCompra.ordenCompra = o) >=
                  (select coalesce(sum(d.subtotal), 0) from ComprasDetalleOrdenCompra d where d.ordenCompra = o))
              or (lower(:buscar) like '%pendient%' and
                  (select coalesce(sum(pg.monto), 0) from ComprasPagoCompra pg where pg.comprobanteCompra.ordenCompra = o) <
                  (select coalesce(sum(d.subtotal), 0) from ComprasDetalleOrdenCompra d where d.ordenCompra = o))
              or (lower(:buscar) like '%esperando%' and not exists
                  (select cc.idComprobante from ComprasComprobanteCompra cc where cc.ordenCompra = o)))
            """)
    Page<OrdenCompra> buscarPagina(@Param("buscar") String buscar, Pageable pageable);
    @Query("""
            select distinct o from ComprasOrdenCompra o
            join fetch o.cotizacion c join fetch c.proveedor p join fetch p.persona
            left join fetch o.detalles d left join fetch d.detalleCotizacion dc
            left join fetch dc.insumo i left join fetch i.unidadMedida
            where p.idPersona = :proveedorId order by o.fechaEmision desc
            """)
    List<OrdenCompra> findAllByProveedor(@Param("proveedorId") Long proveedorId);
    List<OrdenCompra> findByCotizacionIdCotizacionInAndCotizacionProveedorIdPersona(List<Long> cotizacionIds, Long proveedorId);

    interface ResumenOrdenesProjection {
        Long getTotal();
        Long getPendientes();
        java.math.BigDecimal getMontoPendiente();
    }

    @Query(value = """
            SELECT COUNT(*) AS total,
                   NVL(SUM(CASE WHEN NVL(d.total_orden, 0) > NVL(p.total_pagado, 0) THEN 1 ELSE 0 END), 0) AS pendientes,
                   NVL(SUM(GREATEST(NVL(d.total_orden, 0) - NVL(p.total_pagado, 0), 0)), 0) AS montoPendiente
            FROM ORDEN_COMPRA o
            LEFT JOIN (SELECT ID_ORDEN_COMPRA, SUM(SUBTOTAL) total_orden
                       FROM DETALLE_ORDEN_COMPRA GROUP BY ID_ORDEN_COMPRA) d
              ON d.ID_ORDEN_COMPRA = o.ID_ORDEN_COMPRA
            LEFT JOIN (SELECT c.ID_ORDEN_COMPRA, SUM(p.MONTO) total_pagado
                       FROM COMPROBANTE_COMPRA c JOIN PAGO_COMPRA p ON p.ID_COMPROBANTE = c.ID_COMPROBANTE
                       GROUP BY c.ID_ORDEN_COMPRA) p
              ON p.ID_ORDEN_COMPRA = o.ID_ORDEN_COMPRA
            """, nativeQuery = true)
    ResumenOrdenesProjection resumenOrdenes();

    @Query("""
            select new com.example.TrujillanosSAC.compras.dto.OrdenesPorProveedorReporteDTO(
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
    List<OrdenesPorProveedorReporteDTO> reportePorProveedor(@Param("desde") LocalDate desde,
                                                            @Param("hasta") LocalDate hasta);
}
