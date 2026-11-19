package com.example.TrujillanosSAC.usuarios.service.service;

import com.example.TrujillanosSAC.usuarios.dto.PersonaRequestDTO;
import com.example.TrujillanosSAC.usuarios.dto.PersonaResponseDTO;
import com.example.TrujillanosSAC.usuarios.service.generic.CrudService;
import java.util.Optional;

public interface PersonaService extends CrudService<PersonaRequestDTO, PersonaResponseDTO, Long> {
    Optional<PersonaResponseDTO> buscarPorNumeroDocumento(String numeroDocumento);
}
