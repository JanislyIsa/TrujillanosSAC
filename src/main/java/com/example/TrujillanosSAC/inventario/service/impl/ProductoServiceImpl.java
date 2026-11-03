package com.example.TrujillanosSAC.inventario.service.impl;

import com.example.TrujillanosSAC.inventario.model.Producto;
import com.example.TrujillanosSAC.inventario.repository.ProductoRepository;
import com.example.TrujillanosSAC.inventario.service.ProductoService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ProductoServiceImpl implements ProductoService {

    private final ProductoRepository productoRepository;

    public ProductoServiceImpl(ProductoRepository productoRepository) {
        this.productoRepository = productoRepository;
    }

    @Override
    public List<Producto> listarProductos() {
        return productoRepository.findAllConPresentacion().stream().filter(p -> Boolean.TRUE.equals(p.getActivo())).toList();
    }

    @Override
    public Producto obtenerProductoPorId(Long id) {
        return productoRepository.findByIdConPresentacion(id)
                .orElseThrow(() -> new RuntimeException("Producto no encontrado"));
    }

    @Override
    public List<Producto> listarPresentacionesDelProducto(Long id) {
        Producto producto = obtenerProductoPorId(id);
        return productoRepository.findByNombreConPresentacion(producto.getNombre()).stream().filter(p -> Boolean.TRUE.equals(p.getActivo())).toList();
    }
}
