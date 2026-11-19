package com.example.TrujillanosSAC.compras.repository;

import com.example.TrujillanosSAC.compras.model.Cotizacion;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

@org.springframework.stereotype.Repository("comprasCotizacionRepository")
public interface CotizacionRepository extends JpaRepository<Cotizacion, Long> {
    List<Cotizacion> findBySolicitudCompraIdSolicitud(Long solicitudId);
    List<Cotizacion> findByProveedorIdPersona(Long proveedorId);
    long countByProveedorIdPersona(Long proveedorId);
    Page<Cotizacion> findByProveedorIdPersona(Long proveedorId, Pageable pageable);
    @Query(value = """
            select c from ComprasCotizacion c
            join c.proveedor p join p.persona persona
            where (:buscar = '' or lower(concat(concat(persona.nombre, ' '), persona.apellido)) like lower(concat('%', :buscar, '%'))
              or lower(c.estadoCotizacion.nombre) like lower(concat('%', :buscar, '%'))
              or lower(concat('co_', function('TO_CHAR', c.idCotizacion))) like lower(concat('%', :buscar, '%'))
              or lower(concat('sc_', function('TO_CHAR', c.solicitudCompra.idSolicitud))) like lower(concat('%', :buscar, '%')))
            """,
            countQuery = """
            select count(c) from ComprasCotizacion c
            join c.proveedor p join p.persona persona
            where (:buscar = '' or lower(concat(concat(persona.nombre, ' '), persona.apellido)) like lower(concat('%', :buscar, '%'))
              or lower(c.estadoCotizacion.nombre) like lower(concat('%', :buscar, '%'))
              or lower(concat('co_', function('TO_CHAR', c.idCotizacion))) like lower(concat('%', :buscar, '%'))
              or lower(concat('sc_', function('TO_CHAR', c.solicitudCompra.idSolicitud))) like lower(concat('%', :buscar, '%')))
            """)
    Page<Cotizacion> buscarPagina(@Param("buscar") String buscar, Pageable pageable);

    @Query(value = """
            select c from ComprasCotizacion c
            where c.proveedor.idPersona = :proveedorId
              and (:buscar = '' or lower(c.estadoCotizacion.nombre) like lower(concat('%', :buscar, '%'))
                or lower(concat('sc_', function('TO_CHAR', c.solicitudCompra.idSolicitud))) like lower(concat('%', :buscar, '%')))
            """,
            countQuery = """
            select count(c) from ComprasCotizacion c
            where c.proveedor.idPersona = :proveedorId
              and (:buscar = '' or lower(c.estadoCotizacion.nombre) like lower(concat('%', :buscar, '%'))
                or lower(concat('sc_', function('TO_CHAR', c.solicitudCompra.idSolicitud))) like lower(concat('%', :buscar, '%')))
            """)
    Page<Cotizacion> buscarPaginaPorProveedor(@Param("proveedorId") Long proveedorId,
            @Param("buscar") String buscar, Pageable pageable);
    boolean existsBySolicitudCompraIdSolicitudAndProveedorIdPersona(Long solicitudId, Long proveedorId);
    @Query("""
            select count(c) from ComprasCotizacion c
            where lower(c.estadoCotizacion.nombre) <> 'rechazada'
              and not exists (select o.idOrdenCompra from ComprasOrdenCompra o where o.cotizacion = c)
            """)
    long countPendientes();
    long countByProveedorIdPersonaAndEstadoCotizacionNombreIgnoreCase(Long proveedorId, String estado);
}
