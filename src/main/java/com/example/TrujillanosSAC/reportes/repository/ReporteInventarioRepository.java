package com.example.TrujillanosSAC.reportes.repository;

import com.example.TrujillanosSAC.inventario.model.Movimiento;
import com.example.TrujillanosSAC.reportes.dto.MovimientoInventarioReporteDTO;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.Repository;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;

public interface ReporteInventarioRepository extends Repository<Movimiento, Long> {

    @Query("""
            select new com.example.TrujillanosSAC.reportes.dto.MovimientoInventarioReporteDTO(
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
    List<MovimientoInventarioReporteDTO> movimientos(@Param("desde") LocalDate desde,
                                                    @Param("hasta") LocalDate hasta);
}
