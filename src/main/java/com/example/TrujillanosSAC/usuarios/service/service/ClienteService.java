package com.example.TrujillanosSAC.usuarios.service.service;

import com.example.TrujillanosSAC.usuarios.dto.ClienteRequestDTO;
import com.example.TrujillanosSAC.usuarios.dto.ClienteResponseDTO;
import com.example.TrujillanosSAC.usuarios.service.generic.CrudService;

public interface ClienteService extends CrudService<ClienteRequestDTO, ClienteResponseDTO, Long> {
}
