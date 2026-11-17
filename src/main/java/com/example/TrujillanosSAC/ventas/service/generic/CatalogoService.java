package com.example.TrujillanosSAC.ventas.service.generic;

import com.example.TrujillanosSAC.ventas.dto.request.CatalogoRequestDTO;
import com.example.TrujillanosSAC.ventas.dto.response.CatalogoResponseDTO;
import com.example.TrujillanosSAC.ventas.model.Catalogo;
import com.example.TrujillanosSAC.ventas.model.codigo.CodigoCatalogo;

import java.util.List;
import java.util.Optional;

/**
 * Contrato común de los 5 catálogos de ventas (estados, tipos y métodos de pago).
 * Cumple el mismo papel que CrudService: cada catálogo lo extiende en service/service
 * y lo implementa en service/impl a partir de CatalogoServiceImpl.
 */
public interface CatalogoService<E extends Catalogo> {

    List<CatalogoResponseDTO> listar();

    CatalogoResponseDTO obtener(Long id);

    /** Para los demás servicios: busca el registro que el programa necesita por su código. */
    E porCodigo(CodigoCatalogo codigo);

    /** Igual que porCodigo, pero sin lanzar error si falta. */
    Optional<E> buscarPorCodigo(CodigoCatalogo codigo);

    /** Cambia solo el nombre visible; el código no cambia. */
    CatalogoResponseDTO actualizar(Long id, CatalogoRequestDTO request);
}
