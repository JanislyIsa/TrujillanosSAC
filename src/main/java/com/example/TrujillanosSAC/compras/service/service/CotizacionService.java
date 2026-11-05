package com.example.TrujillanosSAC.compras.service.service;

import com.example.TrujillanosSAC.compras.dto.CotizacionRequestDTO;
import com.example.TrujillanosSAC.compras.dto.CotizacionResponseDTO;
import com.example.TrujillanosSAC.compras.service.generic.CrudService;
import java.util.List;

public interface CotizacionService extends CrudService<CotizacionRequestDTO, CotizacionResponseDTO, Long> {
    List<CotizacionResponseDTO> listarPorSolicitud(Long solicitudId);
    List<CotizacionResponseDTO> listarPorProveedor(Long proveedorId);
    CotizacionResponseDTO aceptar(Long id);
    CotizacionResponseDTO rechazar(Long id);
}
