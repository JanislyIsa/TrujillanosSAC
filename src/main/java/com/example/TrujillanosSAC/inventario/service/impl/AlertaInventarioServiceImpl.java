package com.example.TrujillanosSAC.inventario.service.impl;

import com.example.TrujillanosSAC.usuarios.model.Empleado;
import com.example.TrujillanosSAC.inventario.dto.InventarioDtos.*;
import com.example.TrujillanosSAC.inventario.exception.InventarioException;
import com.example.TrujillanosSAC.inventario.model.*;
import com.example.TrujillanosSAC.inventario.repository.*;
import com.example.TrujillanosSAC.inventario.service.AlertaInventarioService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.text.Normalizer;
import java.time.*;
import java.util.*;
import com.example.TrujillanosSAC.usuarios.repository.*;

@Service @RequiredArgsConstructor
@Transactional(readOnly = true)
public class AlertaInventarioServiceImpl implements AlertaInventarioService {
    private final AlertaRepository alertas;
    private final NotificacionInventarioRepository notificaciones;
    private final InventarioBloqueos bloqueos;
    private final ProductoRepository productos;
    private final InsumoRepository insumos;
    private final EmpleadoRepository empleados;
    private final UsuarioRepository usuarios;
    private final Clock inventarioClock;

    @Override @Transactional
    public void sincronizar(TipoItem tipo, Long id) {
        int stock, minimo, limite;
        String nombre;
        Almacen almacen;
        if (tipo == TipoItem.PRODUCTO) {
            Producto p = bloqueos.producto(id).orElseThrow(() -> InventarioException.noEncontrado("Producto"));
            stock=p.getStock(); minimo=p.getStockMinimo(); limite=p.getStockLimite(); nombre=p.getNombre(); almacen=p.getAlmacen();
        } else {
            Insumo i = bloqueos.insumo(id).orElseThrow(() -> InventarioException.noEncontrado("Insumo"));
            stock=i.getStock(); minimo=i.getStockMinimo(); limite=i.getStockLimite(); nombre=i.getNombre(); almacen=i.getAlmacen();
        }
        EstadoStock estado = EstadoStock.calcular(stock,minimo,limite);
        LocalDateTime ahora = LocalDateTime.now(inventarioClock);
        List<Alerta> activas = alertas.findByTipoItemAndIdItemAndResueltaEnIsNull(tipo.name(), id);
        Alerta actual = null;
        for (Alerta a : activas) {
            if (estado != EstadoStock.NORMAL && estado.name().equals(a.getTipoAlerta()) && actual == null)
                actual = a;
            else a.setResueltaEn(ahora);
        }
        if (estado == EstadoStock.NORMAL) return;
        if (actual == null) {
            actual = Alerta.builder().tipoItem(tipo.name()).idItem(id).tipoAlerta(estado.name())
                    .fecha(ahora.toLocalDate()).fechaHora(ahora).almacen(almacen)
                    .empleado(almacen.getEmpleadoResponsable()).build();
        }
        actual.setDescripcion((estado == EstadoStock.BAJO ? "Stock bajo: " : "Sobrestock: ") + nombre);
        actual.setStockActual(stock); actual.setStockMinimo(minimo); actual.setStockLimite(limite);
        actual = alertas.save(actual);
        for (Empleado jefe : empleados.findAllConCargo().stream().filter(this::esJefeCompras).toList()) {
            if (!notificaciones.existsByAlerta_IdAlertaAndDestinatario_IdPersona(actual.getIdAlerta(),jefe.getIdPersona()))
                notificaciones.save(NotificacionInventario.builder().alerta(actual).destinatario(jefe).creadaEn(ahora).build());
        }
    }

    @Override @Transactional
    public void monitorear() {
        // Orden estable compartido con ventas y solicitudes para reducir interbloqueos.
        productos.findAllParaGestion().forEach(p -> sincronizar(TipoItem.PRODUCTO,p.getIdProducto()));
        insumos.findAllParaInventario().forEach(i -> sincronizar(TipoItem.INSUMO,i.getIdInsumo()));
    }
    @Override
    public List<AlertaResponse> listar(boolean soloActivas) {
        return alertas.findAllByOrderByIdAlertaDesc().stream()
                .filter(a -> a.getTipoItem() != null)
                .filter(a -> !soloActivas || a.getResueltaEn() == null).map(this::mapear).toList();
    }
    @Override
    public List<NotificacionResponse> notificaciones(String username) {
        Long empleado = empleado(username).getIdPersona();
        return notificaciones.findByDestinatario_IdPersonaOrderByIdNotificacionDesc(empleado)
                .stream().map(n -> new NotificacionResponse(n.getIdNotificacion(),
                        mapear(n.getAlerta()), n.getCreadaEn(), n.getLeidaEn())).toList();
    }
    @Override @Transactional
    public void marcarLeida(Long id, String username) {
        var n = notificaciones.findPropiaForUpdate(id, empleado(username).getIdPersona())
                .orElseThrow(() -> InventarioException.noEncontrado("Notificacion"));
        if (n.getLeidaEn() == null) n.setLeidaEn(LocalDateTime.now(inventarioClock));
    }
    private Empleado empleado(String username) {
        return usuarios.findByUsernameConRolesYPersona(username)
                .flatMap(u -> empleados.findByIdPersonaConCargo(u.getPersona().getIdPersona()))
                .orElseThrow(() -> InventarioException.noEncontrado("Empleado"));
    }
    private boolean esJefeCompras(Empleado e) {
        String cargo = Normalizer.normalize(e.getCargo().getNombre(), Normalizer.Form.NFD)
                .replaceAll("\\p{M}", "").trim().toUpperCase(Locale.ROOT).replace(' ','_');
        return cargo.equals("JEFE_COMPRAS") || cargo.equals("JEFE_DE_COMPRAS");
    }
    private AlertaResponse mapear(Alerta a) {
        return new AlertaResponse(a.getIdAlerta(), "ALT-%03d".formatted(a.getIdAlerta()),
                TipoItem.valueOf(a.getTipoItem()), a.getIdItem(), a.getTipoAlerta(), a.getDescripcion(),
                a.getStockActual(), a.getStockMinimo(), a.getStockLimite(), a.getFechaHora(), a.getResueltaEn());
    }
}
