package com.example.TrujillanosSAC.compras.repository;

import com.example.TrujillanosSAC.inventario.model.SolicitudCompra;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

/** Consultas de solicitudes asignadas a proveedores desde el módulo de Compras. */
public interface SolicitudProveedorRepository extends JpaRepository<SolicitudCompra, Long> {
    @Query("""
            select s from SolicitudCompra s
            join fetch s.estadoSolicitudCompra
            join fetch s.proveedor p join fetch p.persona
            where p.idPersona = :proveedorId order by s.idSolicitud desc
            """)
    List<SolicitudCompra> findAllByProveedor(@Param("proveedorId") Long proveedorId);
}
