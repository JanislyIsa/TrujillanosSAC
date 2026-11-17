package com.example.TrujillanosSAC.inventario.repository;

import com.example.TrujillanosSAC.inventario.model.DetalleMovProducto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

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

    @Query("""
            select d from DetalleMovProducto d
            join fetch d.movimiento m left join fetch m.almacen
            join fetch d.producto p left join fetch p.presentacion
            where m.idMovimiento = :idMovimiento
            """)
    Optional<DetalleMovProducto> findByIdMovimiento(@Param("idMovimiento") Long idMovimiento);
}
