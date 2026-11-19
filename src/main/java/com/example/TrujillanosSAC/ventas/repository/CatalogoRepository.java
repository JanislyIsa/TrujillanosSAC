package com.example.TrujillanosSAC.ventas.repository;

import com.example.TrujillanosSAC.ventas.model.Catalogo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.NoRepositoryBean;

import java.util.Optional;

/** Consultas comunes de los catálogos. Spring no crea un bean para esta interfaz, solo para sus hijas. */
@NoRepositoryBean
public interface CatalogoRepository<E extends Catalogo> extends JpaRepository<E, Long> {
    Optional<E> findByCodigo(String codigo);
    Optional<E> findFirstByNombreIgnoreCase(String nombre);
}
