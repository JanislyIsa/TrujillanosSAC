package com.example.TrujillanosSAC.compras.service.service;

import com.example.TrujillanosSAC.compras.dto.OrdenCompraRequestDTO;
import com.example.TrujillanosSAC.compras.dto.OrdenCompraResponseDTO;
import com.example.TrujillanosSAC.compras.dto.OrdenCompraListadoResponseDTO;
import com.example.TrujillanosSAC.compras.dto.OrdenesResumenResponseDTO;
import com.example.TrujillanosSAC.compras.service.generic.CrudService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface OrdenCompraService extends CrudService<OrdenCompraRequestDTO, OrdenCompraResponseDTO, Long> {
    OrdenCompraResponseDTO generarDesdeCotizacion(Long cotizacionId, String descripcion);
    Page<OrdenCompraListadoResponseDTO> listarPagina(String buscar, Pageable pageable);
    OrdenesResumenResponseDTO resumen();
}
