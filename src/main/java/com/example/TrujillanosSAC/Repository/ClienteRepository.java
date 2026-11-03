package com.example.TrujillanosSAC.Repository;

import com.example.TrujillanosSAC.Model.Cliente;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ClienteRepository extends JpaRepository<Cliente, Long> {
}
