package com.example.TrujillanosSAC.ventas.repository;

import com.example.TrujillanosSAC.inventario.model.Producto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

/**
 * Consultas de solo lectura del catálogo de ventas. Está en ventas para no modificar el módulo de inventario.
 * Solo devuelve productos que se pueden pedir: activos y con stock (las mismas reglas que valida PedidoServiceImpl).
 */
public interface ProductoVentaRepository extends JpaRepository<Producto, Long> {

    @Query("""
            select p
            from Producto p
            join fetch p.categoria
            join fetch p.presentacion
            where p.activo = true and p.stock > 0
            order by p.nombre
            """)
    List<Producto> findDisponibles();

    @Query("""
            select p
            from Producto p
            join fetch p.categoria
            join fetch p.presentacion
            where p.idProducto = :id and p.activo = true and p.stock > 0
            """)
    Optional<Producto> findDisponibleById(@Param("id") Long id);
}
