package com.example.TrujillanosSAC.ventas.service.impl;

import com.example.TrujillanosSAC.exception.RecursosNoEncontradosException;
import com.example.TrujillanosSAC.inventario.model.Producto;
import com.example.TrujillanosSAC.ventas.dto.response.ProductoVentaResponseDTO;
import com.example.TrujillanosSAC.ventas.repository.ProductoVentaRepository;
import com.example.TrujillanosSAC.ventas.service.ProductoVentaService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service("ventasProductoVentaService")
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ProductoVentaServiceImpl implements ProductoVentaService {

    private final ProductoVentaRepository productos;

    @Override
    public List<ProductoVentaResponseDTO> listarDisponibles() {
        return productos.findDisponibles().stream().map(this::aDto).toList();
    }

    @Override
    public ProductoVentaResponseDTO obtenerDisponible(Long id) {
        return productos.findDisponibleById(id).map(this::aDto).orElseThrow(() ->
                new RecursosNoEncontradosException("Producto no disponible: " + id));
    }

    private ProductoVentaResponseDTO aDto(Producto p) {
        return new ProductoVentaResponseDTO(p.getIdProducto(), p.getNombre(), p.getDescripcion(), p.getPrecio(),
                p.getStock(), p.getCategoria().getNombre(), p.getPresentacion().getNombre(), p.getImagenUrl());
    }
}
