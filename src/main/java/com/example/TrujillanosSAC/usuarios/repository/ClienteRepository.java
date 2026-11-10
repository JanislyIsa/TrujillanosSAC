package com.example.TrujillanosSAC.usuarios.repository;

import com.example.TrujillanosSAC.usuarios.model.Cliente;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ClienteRepository extends JpaRepository<Cliente, Long> {
}
