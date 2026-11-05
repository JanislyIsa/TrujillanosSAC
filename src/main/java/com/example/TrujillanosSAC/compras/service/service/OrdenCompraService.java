package com.example.TrujillanosSAC.compras.service.service;

import com.example.TrujillanosSAC.compras.dto.OrdenCompraRequestDTO;
import com.example.TrujillanosSAC.compras.dto.OrdenCompraResponseDTO;
import com.example.TrujillanosSAC.compras.service.generic.CrudService;

public interface OrdenCompraService extends CrudService<OrdenCompraRequestDTO, OrdenCompraResponseDTO, Long> {
    OrdenCompraResponseDTO generarDesdeCotizacion(Long cotizacionId, String descripcion);
}
