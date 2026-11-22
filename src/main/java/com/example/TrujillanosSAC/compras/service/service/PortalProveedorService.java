package com.example.TrujillanosSAC.compras.service.service;

import com.example.TrujillanosSAC.compras.dto.CotizacionProveedorRequestDTO;
import com.example.TrujillanosSAC.compras.dto.CotizacionResponseDTO;
import com.example.TrujillanosSAC.compras.dto.OrdenProveedorResponseDTO;
import com.example.TrujillanosSAC.compras.dto.SolicitudProveedorResponseDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import java.util.List;

public interface PortalProveedorService {
    Long proveedorId(String username);
    CotizacionResponseDTO enviarCotizacion(CotizacionProveedorRequestDTO request, String username);
    Page<SolicitudProveedorResponseDTO> solicitudes(String username, String buscar, Pageable pageable);
    SolicitudProveedorResponseDTO solicitud(Long id, String username);
    List<OrdenProveedorResponseDTO> ordenes(List<Long> cotizacionIds, String username);
}
