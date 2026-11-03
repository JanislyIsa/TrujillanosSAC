package com.example.TrujillanosSAC.inventario.service.impl;

import com.example.TrujillanosSAC.inventario.model.*;
import com.example.TrujillanosSAC.inventario.repository.*;
import jakarta.persistence.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import java.util.Optional;

@Component @RequiredArgsConstructor
public class InventarioBloqueos {
    private final ProductoRepository productos;
    private final InsumoRepository insumos;
    private final EntityManager em;

    public Optional<Producto> producto(Long id) {
        em.flush();
        return productos.findByIdForUpdate(id).map(this::actualizar);
    }
    public Optional<Insumo> insumo(Long id) {
        em.flush();
        return insumos.findByIdForUpdate(id).map(this::actualizar);
    }
    private <T> T actualizar(T item) {
        // Una consulta con bloqueo puede reutilizar una entidad cargada antes de esperar.
        // Refrescar dentro del bloqueo asegura calcular con el saldo ya confirmado.
        em.refresh(item,LockModeType.PESSIMISTIC_WRITE);
        return item;
    }
}
