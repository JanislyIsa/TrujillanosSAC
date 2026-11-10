package com.example.TrujillanosSAC.ventas.service;

import com.example.TrujillanosSAC.exception.RecursosNoEncontradosException;
import com.example.TrujillanosSAC.exception.ReglaNegocioException;
import com.example.TrujillanosSAC.ventas.dto.request.CatalogoRequestDTO;
import com.example.TrujillanosSAC.ventas.dto.response.CatalogoResponseDTO;
import com.example.TrujillanosSAC.ventas.mapper.VentaMapper;
import com.example.TrujillanosSAC.ventas.model.Catalogo;
import com.example.TrujillanosSAC.ventas.model.codigo.CodigoCatalogo;
import com.example.TrujillanosSAC.ventas.repository.CatalogoRepository;
import org.springframework.data.domain.Sort;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

/**
 * Lógica común de los 5 catálogos de ventas. Cada catálogo es una subclase de pocas líneas
 * (por ejemplo {@link EstadoPedidoService}). Los registros los crea el sistema (seeder o script SQL)
 * porque el programa depende de sus códigos; por eso solo se pueden consultar y renombrar.
 */
public abstract class CatalogoService<E extends Catalogo> {

    private final CatalogoRepository<E> repository;
    private final String nombreCatalogo;

    protected CatalogoService(CatalogoRepository<E> repository, String nombreCatalogo) {
        this.repository = repository;
        this.nombreCatalogo = nombreCatalogo;
    }

    @Transactional(readOnly = true)
    public List<CatalogoResponseDTO> listar() {
        return repository.findAll(Sort.by("id")).stream().map(VentaMapper::catalogo).toList();
    }

    @Transactional(readOnly = true)
    public CatalogoResponseDTO obtener(Long id) {
        return VentaMapper.catalogo(buscar(id));
    }

    /** Para los demás servicios: busca el registro que el programa necesita por su código. */
    @Transactional(readOnly = true)
    public E porCodigo(CodigoCatalogo codigo) {
        return buscarPorCodigo(codigo).orElseThrow(() -> new ReglaNegocioException(
                "Falta registrar en " + nombreCatalogo + " el código " + codigo.name()));
    }

    /** Igual que porCodigo, pero sin lanzar error si falta (lanzarlo marcaría la transacción para deshacerse). */
    @Transactional(readOnly = true)
    public Optional<E> buscarPorCodigo(CodigoCatalogo codigo) {
        return repository.findByCodigo(codigo.name());
    }

    /** Cambia solo el nombre visible; el código queda igual para que el programa lo siga encontrando. */
    @Transactional
    public CatalogoResponseDTO actualizar(Long id, CatalogoRequestDTO request) {
        E entidad = buscar(id);
        String nombre = request.nombre().trim();
        repository.findFirstByNombreIgnoreCase(nombre)
                .filter(otro -> !Objects.equals(otro.getId(), entidad.getId()))
                .ifPresent(otro -> {
                    throw new ReglaNegocioException("Ya existe un registro con el nombre " + nombre);
                });
        entidad.setNombre(nombre);
        return VentaMapper.catalogo(entidad);
    }

    private E buscar(Long id) {
        return repository.findById(id).orElseThrow(() ->
                new RecursosNoEncontradosException(nombreCatalogo + " no encontrado: " + id));
    }
}
