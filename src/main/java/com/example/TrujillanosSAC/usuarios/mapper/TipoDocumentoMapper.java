package com.example.TrujillanosSAC.usuarios.mapper;

import com.example.TrujillanosSAC.usuarios.dto.TipoDocumentoResponseDTO;
import com.example.TrujillanosSAC.usuarios.model.TipoDocumento;
import org.springframework.stereotype.Component;

@Component
public class TipoDocumentoMapper {
    public TipoDocumentoResponseDTO toResponse(TipoDocumento tipo) {
        return new TipoDocumentoResponseDTO(tipo.getIdDocumento(), tipo.getNombre(), tipo.getAbreviatura(), tipo.getLongitud());
    }
}
