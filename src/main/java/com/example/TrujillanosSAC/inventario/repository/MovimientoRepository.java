package com.example.TrujillanosSAC.inventario.repository;

import com.example.TrujillanosSAC.inventario.model.Movimiento;
import com.example.TrujillanosSAC.inventario.dto.ReporteMovimientoInventarioDto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;

public interface MovimientoRepository extends JpaRepository<Movimiento, Long> {
    @Query("""
            select m
            from Movimiento m
            left join fetch m.almacen
            order by m.idMovimiento desc
            """)
    List<Movimiento> findAllParaInventario();

    @Query("""
            select new com.example.TrujillanosSAC.inventario.dto.ReporteMovimientoInventarioDto(
                m.idMovimiento,
                m.tipoMovimiento,
                m.fecha,
                m.hora,
                almacen.nombre,
                coalesce((select sum(dp.cantidad) from DetalleMovProducto dp where dp.movimiento.idMovimiento = m.idMovimiento), 0L),
                coalesce((select sum(di.cantidad) from DetalleMovInsumo di where di.movimiento.idMovimiento = m.idMovimiento), 0L),
                coalesce((select sum(dp.cantidad) from DetalleMovProducto dp where dp.movimiento.idMovimiento = m.idMovimiento), 0L)
                    + coalesce((select sum(di.cantidad) from DetalleMovInsumo di where di.movimiento.idMovimiento = m.idMovimiento), 0L)
            )
            from Movimiento m
            join m.almacen almacen
            where (:desde is null or m.fecha >= :desde)
              and (:hasta is null or m.fecha <= :hasta)
            order by m.fecha desc, m.hora desc
            """)
    List<ReporteMovimientoInventarioDto> reporteMovimientos(@Param("desde") LocalDate desde,
                                                            @Param("hasta") LocalDate hasta);
}
