package com.example.TrujillanosSAC.inventario.service;
import com.example.TrujillanosSAC.inventario.dto.InventarioDtos.*;
import java.util.List;

public interface SolicitudInventarioService {
    List<FaltanteResponse> faltantes();
    SolicitudResponse crear(SolicitudRequest request, String username);
    SolicitudResponse automatica(Long idProveedor, String username);
    List<SolicitudResponse> listar();
    SolicitudResponse obtener(Long id);
}
