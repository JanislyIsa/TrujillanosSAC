package com.example.TrujillanosSAC.ventas.controller;

import com.example.TrujillanosSAC.ventas.dto.request.CatalogoRequestDTO;
import com.example.TrujillanosSAC.ventas.dto.response.CatalogoResponseDTO;
import com.example.TrujillanosSAC.ventas.model.Catalogo;
import com.example.TrujillanosSAC.ventas.service.CatalogoService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Endpoints comunes de los catálogos: consultar (cualquier usuario con sesión, el frontend
 * los necesita para mostrar opciones) y renombrar (solo empleados).
 * Las subclases solo definen la ruta con @RequestMapping.
 */
public abstract class CatalogoController<E extends Catalogo> {

    private final CatalogoService<E> service;

    protected CatalogoController(CatalogoService<E> service) {
        this.service = service;
    }

    @GetMapping
    @PreAuthorize("isAuthenticated()")
    public List<CatalogoResponseDTO> listar() {
        return service.listar();
    }

    @GetMapping("/{id}")
    @PreAuthorize("isAuthenticated()")
    public CatalogoResponseDTO obtener(@PathVariable("id") @Positive Long id) {
        return service.obtener(id);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('EMPLEADO')")
    public CatalogoResponseDTO actualizar(@PathVariable("id") @Positive Long id,
                                          @Valid @RequestBody CatalogoRequestDTO request) {
        return service.actualizar(id, request);
    }
}
