package com.example.TrujillanosSAC.inventario.repository;

import com.example.TrujillanosSAC.inventario.model.DetalleMovInsumo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface DetalleMovInsumoRepository extends JpaRepository<DetalleMovInsumo, Long> {
    @Query("""
            select d
            from DetalleMovInsumo d
            join fetch d.movimiento m
            left join fetch m.almacen
            join fetch d.insumo i
            left join fetch i.unidadMedida
            order by m.idMovimiento desc
            """)
    List<DetalleMovInsumo> findAllParaInventario();

    @Query("""
            select d from DetalleMovInsumo d
            join fetch d.movimiento m left join fetch m.almacen
            join fetch d.insumo i left join fetch i.unidadMedida
            where m.idMovimiento = :idMovimiento
            """)
    Optional<DetalleMovInsumo> findByIdMovimiento(@Param("idMovimiento") Long idMovimiento);
}
