package com.example.TrujillanosSAC.Service;

import com.example.TrujillanosSAC.Dto.AdminModuleDto;

import java.util.List;

public interface AdminAccessService {
    boolean puedeAbrirPanel(String username);

    String obtenerCargo(String username);

    List<AdminModuleDto> obtenerModulos(String username);

    boolean puedeAccederA(String username, String url);
}
