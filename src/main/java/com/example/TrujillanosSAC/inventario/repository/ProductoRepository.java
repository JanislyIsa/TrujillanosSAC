package com.example.TrujillanosSAC.inventario.repository;

import com.example.TrujillanosSAC.inventario.model.Producto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface ProductoRepository extends JpaRepository<Producto, Long> {
    @org.springframework.data.jpa.repository.EntityGraph(attributePaths = {"presentacion", "categoria", "almacen"})
    @Query("""
            select p from Producto p
            where ((:buscar is null and :id is null)
                   or (:buscar is not null and locate(:buscar, lower(p.nombre)) > 0)
                   or (:id is not null and p.idProducto = :id))
              and (:almacen is null or p.almacen.idAlmacen = :almacen)
              and (:activo is null or p.activo = :activo)
              and (:estado is null
                   or (:estado = 'BAJO' and p.stock < p.stockMinimo)
                   or (:estado = 'NORMAL' and p.stock >= p.stockMinimo and p.stock <= p.stockLimite)
                   or (:estado = 'SOBRESTOCK' and p.stock > p.stockLimite))
            """)
    Page<Producto> buscarPagina(@Param("buscar") String buscar, @Param("id") Long id,
                                @Param("almacen") Long almacen, @Param("activo") Boolean activo,
                                @Param("estado") String estado, Pageable pageable);

    @org.springframework.data.jpa.repository.EntityGraph(attributePaths = {"presentacion", "categoria"})
    List<Producto> findByPresentacionNombreIgnoreCaseOrderByNombre(String nombrePresentacion);

    @Query("""
            select p
            from Producto p
            left join fetch p.presentacion
            left join fetch p.categoria
            left join fetch p.almacen
            order by p.idProducto
            """)
    List<Producto> findAllParaGestion();

    @Query("select p from Producto p left join fetch p.presentacion where p.idProducto = :id")
    Optional<Producto> findByIdConPresentacion(@Param("id") Long id);

    @Query("""
            select p
            from Producto p
            left join fetch p.presentacion
            where p.nombre = :nombre
            order by p.presentacion.idPresentacion
            """)
    List<Producto> findByNombreConPresentacion(@Param("nombre") String nombre);
    @org.springframework.data.jpa.repository.Lock(jakarta.persistence.LockModeType.PESSIMISTIC_WRITE)
    @Query("select x from Producto x where x.idProducto = :id")
    java.util.Optional<Producto> findByIdForUpdate(@org.springframework.data.repository.query.Param("id") Long id);
}
