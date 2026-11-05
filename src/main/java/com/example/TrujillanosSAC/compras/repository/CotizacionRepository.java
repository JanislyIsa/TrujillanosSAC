package com.example.TrujillanosSAC.compras.repository;

import com.example.TrujillanosSAC.compras.model.Cotizacion;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface CotizacionRepository extends JpaRepository<Cotizacion, Long> {
    List<Cotizacion> findBySolicitudCompraIdSolicitud(Long solicitudId);
    List<Cotizacion> findByProveedorIdPersona(Long proveedorId);
}
