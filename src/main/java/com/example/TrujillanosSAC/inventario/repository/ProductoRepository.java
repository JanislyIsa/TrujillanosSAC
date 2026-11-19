package com.example.TrujillanosSAC.inventario.repository;

import com.example.TrujillanosSAC.inventario.model.Producto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface ProductoRepository extends JpaRepository<Producto, Long> {
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
