package com.example.TrujillanosSAC.inventario.service;
import com.example.TrujillanosSAC.inventario.dto.InventarioDtos.*;
import com.example.TrujillanosSAC.inventario.dto.MovimientoInventarioDto;
import com.example.TrujillanosSAC.inventario.model.*;
import java.time.LocalDate;
import java.util.List;

public interface MovimientoInventarioService {
    ComprobanteResponse registrar(MovimientoRequest request, String responsable);
    ComprobanteResponse comprobante(Long id);
    List<MovimientoInventarioDto> listar(TipoItem tipo, Long idItem, TipoMovimiento movimiento,
            LocalDate desde, LocalDate hasta, Long almacen);
}
