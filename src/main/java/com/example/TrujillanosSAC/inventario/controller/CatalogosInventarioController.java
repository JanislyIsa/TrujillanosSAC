package com.example.TrujillanosSAC.inventario.controller;

import com.example.TrujillanosSAC.inventario.dto.response.CatalogoResponse;
import com.example.TrujillanosSAC.inventario.service.service.MaestrosInventarioService;
import com.example.TrujillanosSAC.usuarios.repository.ProveedorRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/inventario")
public class CatalogosInventarioController {
    private final MaestrosInventarioService maestros;
    private final ProveedorRepository proveedores;

    @GetMapping("/proveedores")
    @PreAuthorize("hasRole('EMPLEADO')")
    @Transactional(readOnly = true)
    public List<CatalogoResponse> proveedores() {
        return proveedores.findAll().stream()
                .map(p -> new CatalogoResponse(p.getIdPersona(),
                        p.getPersona().getNombre() + " " + p.getPersona().getApellido()))
                .toList();
    }

    @GetMapping("/catalogos")
    @PreAuthorize("hasRole('EMPLEADO')")
    public Map<String, List<CatalogoResponse>> catalogos() {
        return maestros.catalogos();
    }
}
