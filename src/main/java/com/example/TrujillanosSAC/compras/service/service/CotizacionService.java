package com.example.TrujillanosSAC.compras.service.service;

import com.example.TrujillanosSAC.compras.dto.CotizacionRequestDTO;
import com.example.TrujillanosSAC.compras.dto.CotizacionResponseDTO;
import com.example.TrujillanosSAC.compras.dto.CotizacionesResumenResponseDTO;
import com.example.TrujillanosSAC.compras.service.generic.CrudService;
import java.util.List;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Page;

public interface CotizacionService extends CrudService<CotizacionRequestDTO, CotizacionResponseDTO, Long> {
    List<CotizacionResponseDTO> listarPorSolicitud(Long solicitudId);
    List<CotizacionResponseDTO> listarPorProveedor(Long proveedorId);
    Page<CotizacionResponseDTO> listarPagina(String buscar, Pageable pageable);
    Page<CotizacionResponseDTO> listarPaginaPorProveedor(Long proveedorId, String buscar, Pageable pageable);
    long contarPendientes();
    CotizacionesResumenResponseDTO resumen();
    CotizacionesResumenResponseDTO resumenPorProveedor(Long proveedorId);
    CotizacionResponseDTO aceptar(Long id);
    CotizacionResponseDTO rechazar(Long id);
}
