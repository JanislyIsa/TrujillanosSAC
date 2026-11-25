package com.example.TrujillanosSAC.inventario.repository;

import com.example.TrujillanosSAC.inventario.model.Insumo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface InsumoRepository extends JpaRepository<Insumo, Long> {
    @org.springframework.data.jpa.repository.EntityGraph(attributePaths = {"unidadMedida", "almacen"})
    @Query("""
            select i from Insumo i
            where ((:buscar is null and :id is null)
                   or (:buscar is not null and locate(:buscar, lower(i.nombre)) > 0)
                   or (:id is not null and i.idInsumo = :id))
              and (:almacen is null or i.almacen.idAlmacen = :almacen)
              and (:activo is null or i.activo = :activo)
              and (:estado is null
                   or (:estado = 'BAJO' and i.stock < i.stockMinimo)
                   or (:estado = 'NORMAL' and i.stock >= i.stockMinimo and i.stock <= i.stockLimite)
                   or (:estado = 'SOBRESTOCK' and i.stock > i.stockLimite))
            """)
    Page<Insumo> buscarPagina(@Param("buscar") String buscar, @Param("id") Long id,
                              @Param("almacen") Long almacen, @Param("activo") Boolean activo,
                              @Param("estado") String estado, Pageable pageable);

    @Query("""
            select i
            from Insumo i
            left join fetch i.unidadMedida
            left join fetch i.almacen
            order by i.idInsumo
            """)
    List<Insumo> findAllParaInventario();
    @org.springframework.data.jpa.repository.Lock(jakarta.persistence.LockModeType.PESSIMISTIC_WRITE)
    @Query("select x from Insumo x where x.idInsumo = :id")
    java.util.Optional<Insumo> findByIdForUpdate(@org.springframework.data.repository.query.Param("id") Long id);
}
