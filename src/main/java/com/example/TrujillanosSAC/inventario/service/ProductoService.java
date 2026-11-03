package com.example.TrujillanosSAC.inventario.service;

import com.example.TrujillanosSAC.inventario.model.Producto;

import java.util.List;

public interface ProductoService {
    List<Producto> listarProductos();

    Producto obtenerProductoPorId(Long id);

    List<Producto> listarPresentacionesDelProducto(Long id);
}
