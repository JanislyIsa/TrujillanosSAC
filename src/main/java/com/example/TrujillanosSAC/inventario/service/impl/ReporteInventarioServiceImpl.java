package com.example.TrujillanosSAC.inventario.service.impl;

import com.example.TrujillanosSAC.exception.ReglaNegocioException;
import com.example.TrujillanosSAC.inventario.dto.ReporteMovimientoInventarioDto;
import com.example.TrujillanosSAC.inventario.repository.MovimientoRepository;
import com.example.TrujillanosSAC.inventario.service.service.ReporteInventarioService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ReporteInventarioServiceImpl implements ReporteInventarioService {

    private final MovimientoRepository movimientosRepository;

    @Override
    public List<ReporteMovimientoInventarioDto> movimientos(LocalDate desde, LocalDate hasta) {
        if (desde != null && hasta != null && desde.isAfter(hasta)) {
            throw new ReglaNegocioException("La fecha desde no puede ser posterior a la fecha hasta");
        }
        return movimientosRepository.reporteMovimientos(desde, hasta);
    }
}
