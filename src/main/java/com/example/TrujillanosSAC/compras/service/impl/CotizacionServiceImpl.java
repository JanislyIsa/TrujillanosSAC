package com.example.TrujillanosSAC.compras.service.impl;

import com.example.TrujillanosSAC.compras.dto.CotizacionRequestDTO;
import com.example.TrujillanosSAC.compras.dto.CotizacionResponseDTO;
import com.example.TrujillanosSAC.compras.dto.CotizacionesResumenResponseDTO;
import com.example.TrujillanosSAC.compras.mapper.CotizacionMapper;
import com.example.TrujillanosSAC.compras.model.Cotizacion;
import com.example.TrujillanosSAC.compras.model.DetalleCotizacion;
import com.example.TrujillanosSAC.compras.repository.CotizacionRepository;
import com.example.TrujillanosSAC.compras.repository.DetalleCotizacionRepository;
import com.example.TrujillanosSAC.compras.repository.EstadoCotizacionRepository;
import com.example.TrujillanosSAC.compras.repository.OrdenCompraRepository;
import com.example.TrujillanosSAC.compras.service.service.CotizacionService;
import com.example.TrujillanosSAC.exception.RecursosNoEncontradosException;
import com.example.TrujillanosSAC.exception.ReglaNegocioException;
import com.example.TrujillanosSAC.usuarios.model.Proveedor;
import com.example.TrujillanosSAC.usuarios.repository.ProveedorRepository;
import com.example.TrujillanosSAC.inventario.model.DetalleSolicitud;
import com.example.TrujillanosSAC.inventario.model.SolicitudCompra;
import com.example.TrujillanosSAC.inventario.repository.DetalleSolicitudRepository;
import com.example.TrujillanosSAC.inventario.repository.SolicitudCompraRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class CotizacionServiceImpl implements CotizacionService {
    private final CotizacionRepository cotizacionRepository;
    private final DetalleCotizacionRepository detalleRepository;
    private final SolicitudCompraRepository solicitudRepository;
    private final DetalleSolicitudRepository detalleSolicitudRepository;
    private final ProveedorRepository proveedorRepository;
    private final EstadoCotizacionRepository estadoRepository;
    private final OrdenCompraRepository ordenRepository;
    private final CotizacionMapper mapper;

    public CotizacionServiceImpl(CotizacionRepository cotizacionRepository,
                                 DetalleCotizacionRepository detalleRepository,
                                 SolicitudCompraRepository solicitudRepository,
                                 DetalleSolicitudRepository detalleSolicitudRepository,
                                 ProveedorRepository proveedorRepository,
                                 EstadoCotizacionRepository estadoRepository,
                                 OrdenCompraRepository ordenRepository,
                                 CotizacionMapper mapper) {
        this.cotizacionRepository = cotizacionRepository;
        this.detalleRepository = detalleRepository;
        this.solicitudRepository = solicitudRepository;
        this.detalleSolicitudRepository = detalleSolicitudRepository;
        this.proveedorRepository = proveedorRepository;
        this.estadoRepository = estadoRepository;
        this.ordenRepository = ordenRepository;
        this.mapper = mapper;
    }

    @Override @Transactional
    public CotizacionResponseDTO create(CotizacionRequestDTO request) {
        SolicitudCompra solicitud = solicitudRepository.findById(request.solicitudId())
                .orElseThrow(() -> notFound("Solicitud de compra", request.solicitudId()));
        Proveedor proveedor = proveedorRepository.findById(request.proveedorId())
                .orElseThrow(() -> notFound("Proveedor", request.proveedorId()));
        if (solicitud.getProveedor() == null || !solicitud.getProveedor().getIdPersona().equals(proveedor.getIdPersona())) {
            throw new ReglaNegocioException("La solicitud no está asignada al proveedor indicado");
        }
        if (request.fechaEntrega().isBefore(LocalDate.now())) {
            throw new ReglaNegocioException("La fecha de entrega no puede ser anterior a hoy");
        }
        List<DetalleSolicitud> solicitados = detalleSolicitudRepository.findBySolicitudParaProveedor(solicitud.getIdSolicitud());
        if (solicitados.isEmpty()) throw new ReglaNegocioException("La solicitud no contiene insumos");
        Map<Long, DetalleSolicitud> porInsumo = solicitados.stream().collect(Collectors.toMap(
                d -> d.getInsumo().getIdInsumo(), Function.identity()));
        if (request.detalles().size() != solicitados.size()) {
            throw new ReglaNegocioException("La cotización debe incluir todos los insumos de la solicitud");
        }

        Cotizacion cotizacion = new Cotizacion();
        cotizacion.setFechaCot(LocalDate.now());
        cotizacion.setFechaEntrega(request.fechaEntrega());
        cotizacion.setDescripcion(request.descripcion() == null ? null : request.descripcion().trim());
        cotizacion.setProveedor(proveedor);
        cotizacion.setSolicitudCompra(solicitud);
        cotizacion.setEstadoCotizacion(estado("Pendiente"));
        BigDecimal total = BigDecimal.ZERO;
        for (var item : request.detalles()) {
            DetalleSolicitud solicitado = porInsumo.get(item.insumoId());
            if (solicitado == null) throw new ReglaNegocioException("El insumo " + item.insumoId() + " no pertenece a la solicitud");
            final int cantidad;
            try {
                cantidad = item.cantidad().intValueExact();
            } catch (ArithmeticException ex) {
                throw new ReglaNegocioException("La cantidad cotizada debe ser un número entero dentro del rango permitido");
            }
            BigDecimal subtotal = BigDecimal.valueOf(cantidad).multiply(item.precioUnitario());
            if (cantidad != solicitado.getCantidad()) {
                throw new ReglaNegocioException("La cantidad cotizada debe coincidir con la cantidad solicitada");
            }
            DetalleCotizacion detalle = DetalleCotizacion.builder()
                    .cantidad(cantidad).precioUnitario(item.precioUnitario()).subtotal(subtotal)
                    .insumo(solicitado.getInsumo()).build();
            cotizacion.agregarDetalle(detalle);
            total = total.add(subtotal);
        }
        cotizacion.setMonto(total);
        return mapper.toResponse(cotizacionRepository.save(cotizacion));
    }

    @Override @Transactional
    public CotizacionResponseDTO update(Long id, CotizacionRequestDTO request) {
        Cotizacion existente = buscarEntidad(id);
        if (ordenRepository.findByCotizacionIdCotizacion(id).isPresent())
            throw new ReglaNegocioException("No se puede modificar una cotización que ya tiene orden de compra");
        if ("Aceptada".equalsIgnoreCase(existente.getEstadoCotizacion().getNombre())
                || "Rechazada".equalsIgnoreCase(existente.getEstadoCotizacion().getNombre()))
            throw new ReglaNegocioException("No se puede modificar una cotización ya resuelta");
        if (!existente.getSolicitudCompra().getIdSolicitud().equals(request.solicitudId())
                || !existente.getProveedor().getIdPersona().equals(request.proveedorId()))
            throw new ReglaNegocioException("No se puede cambiar la solicitud o proveedor de una cotización");
        existente.setFechaEntrega(request.fechaEntrega());
        existente.getDetalles().clear();
        Map<Long, DetalleSolicitud> solicitados = detalleSolicitudRepository
                .findBySolicitudParaProveedor(request.solicitudId()).stream()
                .collect(Collectors.toMap(d -> d.getInsumo().getIdInsumo(), Function.identity()));
        BigDecimal total = BigDecimal.ZERO;
        for (var item : request.detalles()) {
            DetalleSolicitud solicitado = solicitados.get(item.insumoId());
            final int cantidad;
            try {
                cantidad = item.cantidad().intValueExact();
            } catch (ArithmeticException ex) {
                throw new ReglaNegocioException("La cantidad cotizada debe ser un número entero dentro del rango permitido");
            }
            if (solicitado == null || solicitado.getCantidad() != cantidad)
                throw new ReglaNegocioException("El detalle cotizado debe corresponder a la solicitud y respetar su cantidad");
            BigDecimal subtotal = BigDecimal.valueOf(cantidad).multiply(item.precioUnitario());
            existente.agregarDetalle(DetalleCotizacion.builder().cantidad(cantidad)
                    .precioUnitario(item.precioUnitario()).subtotal(subtotal).insumo(solicitado.getInsumo()).build());
            total = total.add(subtotal);
        }
        existente.setMonto(total);
        return mapper.toResponse(cotizacionRepository.save(existente));
    }

    @Override @Transactional(readOnly = true)
    public CotizacionResponseDTO read(Long id) { return mapper.toResponse(buscarEntidad(id)); }

    @Override @Transactional
    public void delete(Long id) {
        Cotizacion cotizacion = buscarEntidad(id);
        if (ordenRepository.findByCotizacionIdCotizacion(id).isPresent())
            throw new ReglaNegocioException("No se puede eliminar una cotización que ya tiene orden de compra");
        cotizacionRepository.delete(cotizacion);
    }

    @Override @Transactional(readOnly = true)
    public Iterable<CotizacionResponseDTO> readAll() {
        return cotizacionRepository.findAll().stream().map(mapper::toResponse).toList();
    }

    @Override @Transactional(readOnly = true)
    public List<CotizacionResponseDTO> listarPorSolicitud(Long solicitudId) {
        if (!solicitudRepository.existsById(solicitudId)) throw notFound("Solicitud de compra", solicitudId);
        return cotizacionRepository.findBySolicitudCompraIdSolicitud(solicitudId).stream().map(mapper::toResponse).toList();
    }

    @Override @Transactional(readOnly = true)
    public List<CotizacionResponseDTO> listarPorProveedor(Long proveedorId) {
        if (!proveedorRepository.existsById(proveedorId)) throw notFound("Proveedor", proveedorId);
        return cotizacionRepository.findByProveedorIdPersona(proveedorId).stream().map(mapper::toResponse).toList();
    }

    @Override @Transactional(readOnly = true)
    public Page<CotizacionResponseDTO> listarPagina(String buscar, Pageable pageable) {
        return incluirOrdenes(cotizacionRepository.buscarPagina(buscar == null ? "" : buscar.trim(), pageable));
    }

    @Override @Transactional(readOnly = true)
    public Page<CotizacionResponseDTO> listarPaginaPorProveedor(Long proveedorId, String buscar, Pageable pageable) {
        return incluirOrdenes(cotizacionRepository.buscarPaginaPorProveedor(proveedorId,
                buscar == null ? "" : buscar.trim(), pageable));
    }

    @Override @Transactional(readOnly = true)
    public long contarPendientes() { return cotizacionRepository.countPendientes(); }

    @Override @Transactional(readOnly = true)
    public CotizacionesResumenResponseDTO resumen() {
        long total = cotizacionRepository.count();
        long pendientes = cotizacionRepository.countPendientes();
        return new CotizacionesResumenResponseDTO(total, pendientes, total - pendientes);
    }

    @Override @Transactional(readOnly = true)
    public CotizacionesResumenResponseDTO resumenPorProveedor(Long proveedorId) {
        long pendientes = cotizacionRepository.countByProveedorIdPersonaAndEstadoCotizacionNombreIgnoreCase(proveedorId, "Pendiente")
                + cotizacionRepository.countByProveedorIdPersonaAndEstadoCotizacionNombreIgnoreCase(proveedorId, "En revision")
                + cotizacionRepository.countByProveedorIdPersonaAndEstadoCotizacionNombreIgnoreCase(proveedorId, "En revisión");
        long aprobadas = cotizacionRepository.countByProveedorIdPersonaAndEstadoCotizacionNombreIgnoreCase(proveedorId, "Aceptada");
        long total = cotizacionRepository.countByProveedorIdPersona(proveedorId);
        return new CotizacionesResumenResponseDTO(total, pendientes, aprobadas);
    }

    private Page<CotizacionResponseDTO> incluirOrdenes(Page<Cotizacion> pagina) {
        var ids = pagina.getContent().stream().map(Cotizacion::getIdCotizacion).toList();
        var conOrden = ids.isEmpty() ? java.util.Set.<Long>of()
                : ordenRepository.findByCotizacionIdCotizacionIn(ids).stream()
                    .map(o -> o.getCotizacion().getIdCotizacion()).collect(Collectors.toSet());
        return pagina.map(cotizacion -> {
            var response = mapper.toResponse(cotizacion);
            return new CotizacionResponseDTO(response.id(), response.fechaCotizacion(), response.fechaEntrega(),
                    response.estado(), response.solicitudId(), response.proveedorId(), response.proveedorNombre(),
                    response.monto(), response.detalles(), response.descripcion(), conOrden.contains(cotizacion.getIdCotizacion()));
        });
    }

    @Override @Transactional
    public CotizacionResponseDTO aceptar(Long id) { return cambiarEstado(id, "Aceptada"); }

    @Override @Transactional
    public CotizacionResponseDTO rechazar(Long id) { return cambiarEstado(id, "Rechazada"); }

    private CotizacionResponseDTO cambiarEstado(Long id, String destino) {
        Cotizacion cotizacion = buscarEntidad(id);
        if (ordenRepository.findByCotizacionIdCotizacion(id).isPresent())
            throw new ReglaNegocioException("La cotización ya tiene una orden de compra");
        String estadoActual = cotizacion.getEstadoCotizacion().getNombre();
        if (!"Pendiente".equalsIgnoreCase(estadoActual) && !"En revision".equalsIgnoreCase(estadoActual))
            throw new ReglaNegocioException("Solo se pueden resolver cotizaciones pendientes o en revisión");
        cotizacion.setEstadoCotizacion(estado(destino));
        return mapper.toResponse(cotizacionRepository.save(cotizacion));
    }

    private Cotizacion buscarEntidad(Long id) {
        return cotizacionRepository.findById(id).orElseThrow(() -> notFound("Cotización", id));
    }

    private com.example.TrujillanosSAC.compras.model.EstadoCotizacion estado(String nombre) {
        return estadoRepository.findByNombreIgnoreCase(nombre).orElseThrow(() ->
                new ReglaNegocioException("No está configurado el estado de cotización: " + nombre));
    }

    private RecursosNoEncontradosException notFound(String tipo, Long id) {
        return new RecursosNoEncontradosException(tipo + " no encontrado con id: " + id);
    }
}
