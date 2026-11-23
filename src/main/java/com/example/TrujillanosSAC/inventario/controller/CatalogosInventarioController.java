package com.example.TrujillanosSAC.inventario.controller;
import com.example.TrujillanosSAC.inventario.dto.response.CatalogoResponse;
import com.example.TrujillanosSAC.inventario.service.service.CatalogoInventarioService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Map;

@RestController @RequiredArgsConstructor
@RequestMapping("/api/v1/inventario")
public class CatalogosInventarioController {
    private final CatalogoInventarioService catalogo;
    @GetMapping("/proveedores") @PreAuthorize("hasRole('EMPLEADO')")
    public List<CatalogoResponse> proveedores() { return catalogo.proveedores(); }
    @GetMapping("/catalogos") @PreAuthorize("hasRole('EMPLEADO')")
    public Map<String,List<CatalogoResponse>> catalogos() { return catalogo.catalogos(); }
}
