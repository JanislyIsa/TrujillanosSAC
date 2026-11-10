package com.example.TrujillanosSAC.ventas.service;

import com.example.TrujillanosSAC.ventas.dto.response.ProductoVentaResponseDTO;

import java.util.List;

public interface ProductoVentaService {

    /** Productos activos y con stock, ordenados por nombre. */
    List<ProductoVentaResponseDTO> listarDisponibles();

    /** Un producto disponible; 404 si no existe, está inactivo o no tiene stock. */
    ProductoVentaResponseDTO obtenerDisponible(Long id);
}
