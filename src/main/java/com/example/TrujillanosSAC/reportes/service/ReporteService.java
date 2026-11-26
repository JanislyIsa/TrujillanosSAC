package com.example.TrujillanosSAC.reportes.service;

import com.example.TrujillanosSAC.reportes.dto.MovimientoInventarioReporteDTO;
import com.example.TrujillanosSAC.reportes.dto.OrdenesPorProveedorReporteDTO;
import com.example.TrujillanosSAC.reportes.repository.ReporteComprasRepository;
import com.example.TrujillanosSAC.reportes.repository.ReporteInventarioRepository;
import com.example.TrujillanosSAC.reportes.dto.VentaPorCategoriaDTO;
import com.example.TrujillanosSAC.exception.ReglaNegocioException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ReporteService {

    private final ReporteComprasRepository compras;
    private final ReporteInventarioRepository inventario;
    private final com.example.TrujillanosSAC.ventas.service.service.ReporteService ventas;

    public List<VentaPorCategoriaDTO> ventasPorCategoria(LocalDateTime desde, LocalDateTime hasta) {
        return ventas.ventasPorCategoria(desde, hasta);
    }

    public List<OrdenesPorProveedorReporteDTO> ordenesPorProveedor(LocalDate desde, LocalDate hasta) {
        validarRango(desde, hasta);
        return compras.ordenesPorProveedor(desde, hasta);
    }

    public List<MovimientoInventarioReporteDTO> movimientosInventario(LocalDate desde, LocalDate hasta) {
        validarRango(desde, hasta);
        return inventario.movimientos(desde, hasta);
    }

    private void validarRango(LocalDate desde, LocalDate hasta) {
        if (desde != null && hasta != null && desde.isAfter(hasta)) {
            throw new ReglaNegocioException("La fecha desde no puede ser posterior a la fecha hasta");
        }
    }
}
