package com.example.TrujillanosSAC.compras.service.impl;

import com.example.TrujillanosSAC.compras.dto.OrdenesPorProveedorReporteDTO;
import com.example.TrujillanosSAC.compras.repository.OrdenCompraRepository;
import com.example.TrujillanosSAC.compras.service.service.ReporteComprasService;
import com.example.TrujillanosSAC.exception.ReglaNegocioException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ReporteComprasServiceImpl implements ReporteComprasService {

    private final OrdenCompraRepository ordenes;

    @Override
    public List<OrdenesPorProveedorReporteDTO> ordenesPorProveedor(LocalDate desde, LocalDate hasta) {
        if (desde != null && hasta != null && desde.isAfter(hasta)) {
            throw new ReglaNegocioException("La fecha desde no puede ser posterior a la fecha hasta");
        }
        return ordenes.reportePorProveedor(desde, hasta);
    }
}
