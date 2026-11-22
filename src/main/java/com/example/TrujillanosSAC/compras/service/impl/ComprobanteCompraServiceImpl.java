package com.example.TrujillanosSAC.compras.service.impl;

import com.example.TrujillanosSAC.compras.dto.ComprobanteCompraRequestDTO;
import com.example.TrujillanosSAC.compras.dto.ComprobanteCompraResponseDTO;
import com.example.TrujillanosSAC.compras.mapper.ComprobanteCompraMapper;
import com.example.TrujillanosSAC.compras.model.ComprobanteCompra;
import com.example.TrujillanosSAC.compras.repository.ComprobanteCompraRepository;
import com.example.TrujillanosSAC.compras.repository.EstadoComprobanteCompraRepository;
import com.example.TrujillanosSAC.compras.repository.OrdenCompraRepository;
import com.example.TrujillanosSAC.usuarios.model.Usuario;
import com.example.TrujillanosSAC.usuarios.repository.UsuarioRepository;
import com.example.TrujillanosSAC.compras.service.service.ComprobanteCompraService;
import com.example.TrujillanosSAC.exception.RecursosNoEncontradosException;
import com.example.TrujillanosSAC.exception.ReglaNegocioException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.io.IOException;

@Service
public class ComprobanteCompraServiceImpl implements ComprobanteCompraService {
    private final ComprobanteCompraRepository comprobanteRepository;
    private final OrdenCompraRepository ordenRepository;
    private final EstadoComprobanteCompraRepository estadoRepository;
    private final UsuarioRepository usuarioRepository;
    private final ComprobanteCompraMapper mapper;

    public ComprobanteCompraServiceImpl(ComprobanteCompraRepository comprobanteRepository,
                                        OrdenCompraRepository ordenRepository,
                                        EstadoComprobanteCompraRepository estadoRepository,
                                        UsuarioRepository usuarioRepository,
                                        ComprobanteCompraMapper mapper) {
        this.comprobanteRepository = comprobanteRepository;
        this.ordenRepository = ordenRepository;
        this.estadoRepository = estadoRepository;
        this.usuarioRepository = usuarioRepository;
        this.mapper = mapper;
    }

    @Override @Transactional(readOnly = true)
    public byte[] descargarFactura(Long id, String username, boolean puedeVerTodas) {
        ComprobanteCompra comprobante = buscarEntidad(id);
        if (!puedeVerTodas) {
            Usuario usuario = usuarioRepository.findByUsernameConRolesYPersona(username)
                    .orElseThrow(() -> new RecursosNoEncontradosException("Usuario autenticado no encontrado"));
            if (usuario.getPersona() == null || !comprobante.getOrdenCompra().getCotizacion().getProveedor()
                    .getIdPersona().equals(usuario.getPersona().getIdPersona()))
                throw new ReglaNegocioException("El comprobante no pertenece al proveedor autenticado");
        }
        byte[] archivo = comprobante.getArchivoFactura();
        if (archivo == null || archivo.length == 0)
            throw new RecursosNoEncontradosException("El comprobante no tiene archivo adjunto");
        return archivo;
    }

    @Override @Transactional
    public ComprobanteCompraResponseDTO enviarFacturaProveedor(Long ordenCompraId, String numeroFactura,
                                                                LocalDate fechaEmision, BigDecimal monto,
                                                                MultipartFile archivo, String username) throws IOException {
        if (numeroFactura == null || numeroFactura.isBlank() || numeroFactura.length() > 50)
            throw new ReglaNegocioException("El número de factura es obligatorio y admite hasta 50 caracteres");
        if (archivo == null || archivo.isEmpty() || archivo.getSize() > 10 * 1024 * 1024)
            throw new ReglaNegocioException("Adjunta un PDF de factura de hasta 10 MB");

        byte[] bytes = archivo.getBytes();
        if (bytes.length < 5 || bytes[0] != '%' || bytes[1] != 'P' || bytes[2] != 'D'
                || bytes[3] != 'F' || bytes[4] != '-')
            throw new ReglaNegocioException("El archivo adjunto no es un PDF válido");

        Usuario usuario = usuarioRepository.findByUsernameConRolesYPersona(username)
                .orElseThrow(() -> new RecursosNoEncontradosException("Usuario autenticado no encontrado"));
        if (usuario.getPersona() == null)
            throw new RecursosNoEncontradosException("El usuario no tiene persona asociada");
        var orden = ordenRepository.findById(ordenCompraId).orElseThrow(() ->
                new RecursosNoEncontradosException("Orden de compra no encontrada con id: " + ordenCompraId));
        if (!orden.getCotizacion().getProveedor().getIdPersona().equals(usuario.getPersona().getIdPersona()))
            throw new ReglaNegocioException("La orden no pertenece al proveedor autenticado");

        ComprobanteCompraResponseDTO creado = create(
                new ComprobanteCompraRequestDTO(ordenCompraId, fechaEmision, monto));
        ComprobanteCompra comprobante = buscarEntidad(creado.id());
        comprobante.setNumeroFactura(numeroFactura.trim());
        comprobante.setArchivoFactura(bytes);
        return mapper.toResponse(comprobanteRepository.save(comprobante));
    }

    @Override @Transactional
    public ComprobanteCompraResponseDTO create(ComprobanteCompraRequestDTO request) {
        var orden = ordenRepository.findById(request.ordenCompraId()).orElseThrow(() ->
                new RecursosNoEncontradosException("Orden de compra no encontrada con id: " + request.ordenCompraId()));
        if (comprobanteRepository.existsByOrdenCompraIdOrdenCompra(request.ordenCompraId()))
            throw new ReglaNegocioException("La orden ya tiene un comprobante registrado");
        BigDecimal totalOrden = orden.getDetalles().stream().map(d -> d.getSubtotal())
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        if (request.monto().compareTo(totalOrden) != 0)
            throw new ReglaNegocioException("El monto del comprobante debe coincidir con el total de la orden");
        if (request.fechaEmision().isAfter(LocalDate.now()))
            throw new ReglaNegocioException("La fecha de emisión del comprobante no puede ser futura");

        ComprobanteCompra comprobante = ComprobanteCompra.builder()
                .ordenCompra(orden)
                .fechaEmision(request.fechaEmision())
                .monto(request.monto())
                .estadoComprobanteCompra(estado("Pendiente"))
                .build();
        return mapper.toResponse(comprobanteRepository.save(comprobante));
    }

    @Override @Transactional
    public ComprobanteCompraResponseDTO update(Long id, ComprobanteCompraRequestDTO request) {
        ComprobanteCompra comprobante = buscarEntidad(id);
        if (!comprobante.getOrdenCompra().getIdOrdenCompra().equals(request.ordenCompraId()))
            throw new ReglaNegocioException("No se puede cambiar la orden asociada al comprobante");
        if ("Pagado".equalsIgnoreCase(comprobante.getEstadoComprobanteCompra().getNombre()))
            throw new ReglaNegocioException("No se puede modificar un comprobante pagado");
        BigDecimal totalOrden = comprobante.getOrdenCompra().getDetalles().stream().map(d -> d.getSubtotal())
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        if (request.monto().compareTo(totalOrden) != 0)
            throw new ReglaNegocioException("El monto del comprobante debe coincidir con el total de la orden");
        comprobante.setFechaEmision(request.fechaEmision());
        comprobante.setMonto(request.monto());
        return mapper.toResponse(comprobanteRepository.save(comprobante));
    }

    @Override @Transactional(readOnly = true)
    public ComprobanteCompraResponseDTO read(Long id) { return mapper.toResponse(buscarEntidad(id)); }

    @Override @Transactional
    public void delete(Long id) {
        ComprobanteCompra comprobante = buscarEntidad(id);
        if ("Pagado".equalsIgnoreCase(comprobante.getEstadoComprobanteCompra().getNombre()))
            throw new ReglaNegocioException("No se puede eliminar un comprobante pagado");
        comprobanteRepository.delete(comprobante);
    }

    @Override @Transactional(readOnly = true)
    public Iterable<ComprobanteCompraResponseDTO> readAll() {
        return comprobanteRepository.findAll().stream().map(mapper::toResponse).toList();
    }

    private ComprobanteCompra buscarEntidad(Long id) {
        return comprobanteRepository.findById(id).orElseThrow(() ->
                new RecursosNoEncontradosException("Comprobante de compra no encontrado con id: " + id));
    }

    private com.example.TrujillanosSAC.compras.model.EstadoComprobanteCompra estado(String nombre) {
        return estadoRepository.findByNombreIgnoreCase(nombre).orElseThrow(() ->
                new ReglaNegocioException("No está configurado el estado del comprobante: " + nombre));
    }
}
