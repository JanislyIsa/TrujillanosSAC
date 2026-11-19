package com.example.TrujillanosSAC.usuarios.service.impl;

import com.example.TrujillanosSAC.exception.RecursosNoEncontradosException;
import com.example.TrujillanosSAC.exception.ReglaNegocioException;
import com.example.TrujillanosSAC.usuarios.dto.CargoRequestDTO;
import com.example.TrujillanosSAC.usuarios.dto.CargoResponseDTO;
import com.example.TrujillanosSAC.usuarios.mapper.CargoMapper;
import com.example.TrujillanosSAC.usuarios.model.Cargo;
import com.example.TrujillanosSAC.usuarios.repository.CargoRepository;
import com.example.TrujillanosSAC.usuarios.repository.EmpleadoRepository;
import com.example.TrujillanosSAC.usuarios.service.service.CargoService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class CargoServiceImpl implements CargoService {
    private final CargoRepository repository; private final EmpleadoRepository empleadoRepository; private final CargoMapper mapper;
    public CargoServiceImpl(CargoRepository repository, EmpleadoRepository empleadoRepository, CargoMapper mapper) { this.repository=repository; this.empleadoRepository=empleadoRepository; this.mapper=mapper; }
    @Override @Transactional public CargoResponseDTO create(CargoRequestDTO d) {
        String nombre=d.getNombre().trim(); if(repository.existsByNombreIgnoreCase(nombre)) throw new ReglaNegocioException("Ya existe el cargo " + nombre);
        return mapper.toResponse(repository.save(Cargo.builder().nombre(nombre).build()));
    }
    @Override @Transactional public CargoResponseDTO update(Long id, CargoRequestDTO d) {
        Cargo c=buscar(id); String nombre=d.getNombre().trim(); if(repository.existsByNombreIgnoreCaseAndIdCargoNot(nombre,id)) throw new ReglaNegocioException("Ya existe el cargo " + nombre);
        c.setNombre(nombre); return mapper.toResponse(repository.save(c));
    }
    @Override @Transactional(readOnly=true) public CargoResponseDTO read(Long id) { return mapper.toResponse(buscar(id)); }
    @Override @Transactional public void delete(Long id) { Cargo c=buscar(id); if(empleadoRepository.existsByCargoIdCargo(id)) throw new ReglaNegocioException("No se puede eliminar un cargo asignado a empleados"); repository.delete(c); }
    @Override @Transactional(readOnly=true) public Iterable<CargoResponseDTO> readAll() { return repository.findAll().stream().map(mapper::toResponse).toList(); }
    private Cargo buscar(Long id) { return repository.findById(id).orElseThrow(() -> new RecursosNoEncontradosException("Cargo no encontrado: " + id)); }
}
