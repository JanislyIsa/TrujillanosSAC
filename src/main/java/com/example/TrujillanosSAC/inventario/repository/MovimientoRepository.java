package com.example.TrujillanosSAC.inventario.repository;

import com.example.TrujillanosSAC.inventario.model.Movimiento;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface MovimientoRepository extends JpaRepository<Movimiento, Long> {
    @Query("""
            select m
            from Movimiento m
            left join fetch m.almacen
            order by m.idMovimiento desc
            """)
    List<Movimiento> findAllParaInventario();
}
