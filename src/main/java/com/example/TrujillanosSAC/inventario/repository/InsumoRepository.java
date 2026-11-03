package com.example.TrujillanosSAC.inventario.repository;

import com.example.TrujillanosSAC.inventario.model.Insumo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface InsumoRepository extends JpaRepository<Insumo, Long> {
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
