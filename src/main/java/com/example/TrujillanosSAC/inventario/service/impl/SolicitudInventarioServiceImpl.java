package com.example.TrujillanosSAC.inventario.service.impl;

import com.example.TrujillanosSAC.Repository.*;
import com.example.TrujillanosSAC.inventario.dto.InventarioDtos.*;
import com.example.TrujillanosSAC.inventario.exception.InventarioException;
import com.example.TrujillanosSAC.inventario.model.*;
import com.example.TrujillanosSAC.inventario.repository.*;
import com.example.TrujillanosSAC.inventario.service.SolicitudInventarioService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.math.BigDecimal;
import java.time.*;
import java.util.*;
import com.example.TrujillanosSAC.usuarios.repository.*;

@Service @RequiredArgsConstructor
@Transactional(readOnly = true)
public class SolicitudInventarioServiceImpl implements SolicitudInventarioService {
    private final InventarioBloqueos bloqueos;
    private final InsumoRepository insumos;
    private final SolicitudCompraRepository solicitudes;
    private final DetalleSolicitudRepository detalles;
    private final EstadoSolicitudCompraRepository estados;
    private final ProveedorRepository proveedores;
    private final UsuarioRepository usuarios;
    private final EmpleadoRepository empleados;
    private final InventarioValidacion validacion;
    private final Clock inventarioClock;

    @Override
    public List<FaltanteResponse> faltantes() {
        return insumos.findAllParaInventario().stream().filter(i -> i.getStock() < i.getStockMinimo())
                .map(i -> new FaltanteResponse(i.getIdInsumo(),i.getNombre(),i.getStock(),i.getStockMinimo(),
                        i.getStockLimite(),i.getStockLimite()-i.getStock(),i.getUnidadMedida().getNombre())).toList();
    }
    @Override @Transactional
    public SolicitudResponse crear(SolicitudRequest request, String username) {
        validacion.validar(request);
        Set<Long> ids = new HashSet<>();
        request.detalles().forEach(d -> {
            if (!ids.add(d.idInsumo())) throw new IllegalArgumentException("No repita insumos en la solicitud");
        });
        // Adquirir todos los bloqueos antes de guardar la cabecera.
        Map<Long,Insumo> items = new LinkedHashMap<>();
        ids.stream().sorted().forEach(id -> items.put(id,bloqueos.insumo(id)
                .orElseThrow(() -> InventarioException.noEncontrado("Insumo"))));
        var proveedor = proveedores.findById(request.idProveedor())
                .orElseThrow(() -> InventarioException.noEncontrado("Proveedor"));
        var empleado = usuarios.findByUsernameConRolesYPersona(username)
                .flatMap(u -> empleados.findByIdPersonaConCargo(u.getPersona().getIdPersona()))
                .orElseThrow(() -> InventarioException.noEncontrado("Empleado solicitante"));
        var pendiente = estados.findByNombreIgnoreCase("Pendiente")
                .orElseThrow(() -> InventarioException.conflicto("Configure el estado Pendiente de solicitudes"));
        SolicitudCompra s = solicitudes.save(SolicitudCompra.builder()
                .fechaSolicitud(LocalDate.now(inventarioClock)).estadoSolicitudCompra(pendiente)
                .proveedor(proveedor).empleado(empleado).build());
        for (SolicitudLineaRequest d : request.detalles()) {
            detalles.save(DetalleSolicitud.builder().solicitudCompra(s)
                    .insumo(items.get(d.idInsumo())).cantidad(d.cantidad()).build());
        }
        detalles.flush();
        return mapear(s);
    }
    @Override @Transactional
    public SolicitudResponse automatica(Long idProveedor, String username) {
        // Bloqueos ordenados; la sugerencia se calcula con los saldos actuales.
        List<SolicitudLineaRequest> lineas = new ArrayList<>();
        for (Insumo candidato : insumos.findAllParaInventario()) {
            Insumo i = bloqueos.insumo(candidato.getIdInsumo()).orElseThrow();
            if (i.getStock() < i.getStockMinimo())
                lineas.add(new SolicitudLineaRequest(i.getIdInsumo(),BigDecimal.valueOf(i.getStockLimite()-i.getStock())));
        }
        if (lineas.isEmpty()) throw InventarioException.conflicto("No hay insumos faltantes");
        return crear(new SolicitudRequest(idProveedor,lineas),username);
    }
    @Override
    public List<SolicitudResponse> listar() { return solicitudes.findAllParaProveedor().stream().map(this::mapear).toList(); }
    @Override
    public SolicitudResponse obtener(Long id) {
        return mapear(solicitudes.findById(id).orElseThrow(() -> InventarioException.noEncontrado("Solicitud")));
    }
    private SolicitudResponse mapear(SolicitudCompra s) {
        return new SolicitudResponse(s.getIdSolicitud(),"SC-%03d".formatted(s.getIdSolicitud()),
                s.getFechaSolicitud(),s.getEstadoSolicitudCompra().getNombre(),s.getProveedor().getIdPersona(),
                s.getEmpleado().getIdPersona(),detalles.findBySolicitudParaProveedor(s.getIdSolicitud()).stream()
                .map(d -> new SolicitudDetalleResponse(d.getInsumo().getIdInsumo(),d.getInsumo().getNombre(),
                        d.getCantidad(),d.getInsumo().getUnidadMedida().getNombre())).toList());
    }
}
