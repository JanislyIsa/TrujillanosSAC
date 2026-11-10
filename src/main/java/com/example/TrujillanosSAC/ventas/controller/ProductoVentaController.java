package com.example.TrujillanosSAC.ventas.controller;

import com.example.TrujillanosSAC.ventas.dto.response.ProductoVentaResponseDTO;
import com.example.TrujillanosSAC.ventas.service.ProductoVentaService;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/** Catálogo de golosinas para el cliente: solo lectura, solo productos que se pueden pedir. */
@RestController("ventasProductoVentaController")
@RequestMapping("/api/v1/ventas/productos")
@PreAuthorize("hasAnyRole('EMPLEADO','CLIENTE')")
@RequiredArgsConstructor
public class ProductoVentaController {

    private final ProductoVentaService service;

    @GetMapping
    public List<ProductoVentaResponseDTO> listar() {
        return service.listarDisponibles();
    }

    @GetMapping("/{id}")
    public ProductoVentaResponseDTO obtener(@PathVariable("id") @Positive Long id) {
        return service.obtenerDisponible(id);
    }
}
