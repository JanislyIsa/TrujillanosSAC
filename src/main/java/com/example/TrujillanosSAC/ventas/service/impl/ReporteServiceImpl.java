package com.example.TrujillanosSAC.ventas.service.impl;

import com.example.TrujillanosSAC.ventas.dto.reporte.ProductoMasVendidoDTO;
import com.example.TrujillanosSAC.ventas.dto.reporte.VentaPorCategoriaDTO;
import com.example.TrujillanosSAC.ventas.model.codigo.EstadoPedidoCodigo;
import com.example.TrujillanosSAC.ventas.repository.PedidoRepository;
import com.example.TrujillanosSAC.ventas.service.service.ReporteService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service("ventasReporteService")
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ReporteServiceImpl implements ReporteService {

    private static final Logger log = LoggerFactory.getLogger(ReporteServiceImpl.class);

    /** Códigos de los estados que cuentan como venta: PAGADO, LISTO_PARA_RECOJO y ENTREGADO. */
    private static final List<String> ESTADOS_VENDIDOS =
            EstadoPedidoCodigo.vendidos().stream().map(Enum::name).toList();

    private final PedidoRepository pedidos;

    @Override
    public List<VentaPorCategoriaDTO> ventasPorCategoria(LocalDateTime desde, LocalDateTime hasta) {
        long inicio = System.currentTimeMillis();
        log.info("Reporte ventas por categoría solicitado. desde={}, hasta={}", desde, hasta);
        PedidoServiceImpl.validarRango(desde, hasta);

        List<VentaPorCategoriaDTO> resultado = pedidos.reportePorCategoria(ESTADOS_VENDIDOS, desde, hasta);
        log.info("Reporte ventas por categoría resuelto. filas={}, duracionMs={}",
                resultado.size(), System.currentTimeMillis() - inicio);
        return resultado;
    }

    @Override
    public List<ProductoMasVendidoDTO> productosMasVendidos(LocalDateTime desde, LocalDateTime hasta, int top) {
        long inicio = System.currentTimeMillis();
        log.info("Reporte productos más vendidos solicitado. desde={}, hasta={}, top={}", desde, hasta, top);
        PedidoServiceImpl.validarRango(desde, hasta);

        List<ProductoMasVendidoDTO> resultado = pedidos
                .reporteProductosMasVendidos(ESTADOS_VENDIDOS, desde, hasta)
                .stream().limit(top).toList();
        log.info("Reporte productos más vendidos resuelto. filas={}, duracionMs={}",
                resultado.size(), System.currentTimeMillis() - inicio);
        return resultado;
    }
}
