package com.example.TrujillanosSAC.inventario.service.service;

import com.example.TrujillanosSAC.inventario.dto.response.CatalogoResponse;

import java.util.List;
import java.util.Map;

public interface MaestrosInventarioService {
    Map<String, List<CatalogoResponse>> catalogos();
}
