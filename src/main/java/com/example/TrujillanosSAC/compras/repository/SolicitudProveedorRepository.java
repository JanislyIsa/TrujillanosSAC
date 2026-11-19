package com.example.TrujillanosSAC.compras.repository;

import com.example.TrujillanosSAC.inventario.model.SolicitudCompra;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/** Consultas de solicitudes asignadas a proveedores desde el módulo de Compras. */
public interface SolicitudProveedorRepository extends JpaRepository<SolicitudCompra, Long> {
    @Query("""
            select s from SolicitudCompra s
            join fetch s.estadoSolicitudCompra
            join fetch s.proveedor p join fetch p.persona
            where p.idPersona = :proveedorId order by s.idSolicitud desc
            """)
    List<SolicitudCompra> findAllByProveedor(@Param("proveedorId") Long proveedorId);

    @Query(value = """
            select s from SolicitudCompra s
            join fetch s.estadoSolicitudCompra
            join fetch s.proveedor p join fetch p.persona
            where p.idPersona = :proveedorId
              and (:buscar = '' or lower(s.estadoSolicitudCompra.nombre) like lower(concat('%', :buscar, '%'))
                   or lower(concat('sc_', function('TO_CHAR', s.idSolicitud))) like lower(concat('%', :buscar, '%'))
                   or exists (select d.idDetalleSol from DetalleSolicitud d join d.insumo i
                              where d.solicitudCompra = s and lower(i.nombre) like lower(concat('%', :buscar, '%'))))
              and not exists (select c.idCotizacion from ComprasCotizacion c
                              where c.solicitudCompra = s and c.proveedor.idPersona = :proveedorId)
            """,
            countQuery = """
            select count(s) from SolicitudCompra s
            where s.proveedor.idPersona = :proveedorId
              and (:buscar = '' or lower(s.estadoSolicitudCompra.nombre) like lower(concat('%', :buscar, '%'))
                   or lower(concat('sc_', function('TO_CHAR', s.idSolicitud))) like lower(concat('%', :buscar, '%'))
                   or exists (select d.idDetalleSol from DetalleSolicitud d join d.insumo i
                              where d.solicitudCompra = s and lower(i.nombre) like lower(concat('%', :buscar, '%'))))
              and not exists (select c.idCotizacion from ComprasCotizacion c
                              where c.solicitudCompra = s and c.proveedor.idPersona = :proveedorId)
            """)
    Page<SolicitudCompra> findDisponiblesPorProveedor(@Param("proveedorId") Long proveedorId,
            @Param("buscar") String buscar, Pageable pageable);
}
