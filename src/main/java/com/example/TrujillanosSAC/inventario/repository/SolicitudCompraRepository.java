package com.example.TrujillanosSAC.inventario.repository;

import com.example.TrujillanosSAC.inventario.model.SolicitudCompra;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface SolicitudCompraRepository extends JpaRepository<SolicitudCompra, Long> {
    @Query("""
            select s
            from SolicitudCompra s
            left join fetch s.estadoSolicitudCompra
            left join fetch s.proveedor p
            left join fetch p.persona
            left join fetch s.empleado
            order by s.idSolicitud desc
            """)
    List<SolicitudCompra> findAllParaProveedor();
}
