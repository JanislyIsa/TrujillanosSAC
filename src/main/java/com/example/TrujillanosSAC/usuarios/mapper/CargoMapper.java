package com.example.TrujillanosSAC.usuarios.mapper;

import com.example.TrujillanosSAC.usuarios.dto.CargoResponseDTO;
import com.example.TrujillanosSAC.usuarios.model.Cargo;
import org.springframework.stereotype.Component;

@Component
public class CargoMapper {
    public CargoResponseDTO toResponse(Cargo cargo) { return new CargoResponseDTO(cargo.getIdCargo(), cargo.getNombre()); }
}
