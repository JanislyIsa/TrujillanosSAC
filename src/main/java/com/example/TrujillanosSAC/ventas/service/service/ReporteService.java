package com.example.TrujillanosSAC.ventas.service.service;

import com.example.TrujillanosSAC.reportes.dto.ProductoMasVendidoDTO;
import com.example.TrujillanosSAC.reportes.dto.VentaPorCategoriaDTO;

import java.time.LocalDateTime;
import java.util.List;

/** Reportes agregados de ventas. Solo cuentan los pedidos ya pagados (ver EstadoPedidoCodigo.vendidos()). */
public interface ReporteService {

    List<VentaPorCategoriaDTO> ventasPorCategoria(LocalDateTime desde, LocalDateTime hasta);

    /** @param top cuántos productos devolver como máximo (los de más unidades vendidas primero). */
    List<ProductoMasVendidoDTO> productosMasVendidos(LocalDateTime desde, LocalDateTime hasta, int top);
}
