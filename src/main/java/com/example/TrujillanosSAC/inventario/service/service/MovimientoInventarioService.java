package com.example.TrujillanosSAC.inventario.service.service;
import com.example.TrujillanosSAC.inventario.dto.request.MovimientoRequest;
import com.example.TrujillanosSAC.inventario.dto.response.ComprobanteResponse;
import com.example.TrujillanosSAC.inventario.dto.response.MovimientoResponse;
import com.example.TrujillanosSAC.inventario.model.*;
import java.time.LocalDate;
import java.util.List;

public interface MovimientoInventarioService {
    ComprobanteResponse registrar(MovimientoRequest request, String responsable);
    ComprobanteResponse comprobante(Long id);
    List<MovimientoResponse> listar(TipoItem tipo, Long idItem, TipoMovimiento movimiento,
            LocalDate desde, LocalDate hasta, Long almacen);
}
