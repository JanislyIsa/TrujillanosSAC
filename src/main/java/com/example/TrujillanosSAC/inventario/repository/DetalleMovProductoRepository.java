package com.example.TrujillanosSAC.inventario.repository;

import com.example.TrujillanosSAC.inventario.model.DetalleMovProducto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface DetalleMovProductoRepository extends JpaRepository<DetalleMovProducto, Long> {
    @Query("""
            select d
            from DetalleMovProducto d
            join fetch d.movimiento m
            left join fetch m.almacen
            join fetch d.producto p
            left join fetch p.presentacion
            order by m.idMovimiento desc
            """)
    List<DetalleMovProducto> findAllParaInventario();
}
