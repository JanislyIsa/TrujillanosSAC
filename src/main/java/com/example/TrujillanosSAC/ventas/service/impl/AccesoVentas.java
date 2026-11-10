package com.example.TrujillanosSAC.ventas.service.impl;

import com.example.TrujillanosSAC.Repository.UsuarioRepository;
import com.example.TrujillanosSAC.exception.RecursosNoEncontradosException;
import com.example.TrujillanosSAC.ventas.model.Pedido;
import com.example.TrujillanosSAC.ventas.service.ActorVenta;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Component;

import java.util.Objects;

/** Reglas de acceso compartidas: un cliente solo puede ver y operar sus propios pedidos. */
@Component("ventasAcceso")
@RequiredArgsConstructor
public class AccesoVentas {

    private final UsuarioRepository usuarios;

    /** Id de la persona (y del cliente) del usuario que inició sesión. */
    public Long idPersona(ActorVenta actor) {
        return usuarios.findByUsernameConRolesYPersona(actor.username())
                .map(u -> u.getPersona().getIdPersona())
                .orElseThrow(() -> new RecursosNoEncontradosException("Usuario no encontrado"));
    }

    public void verificarPedido(Pedido pedido, ActorVenta actor) {
        if (actor.empleado()) {
            return;
        }
        if (!Objects.equals(pedido.getCliente().getIdPersona(), idPersona(actor))) {
            throw new AccessDeniedException("El pedido pertenece a otro cliente");
        }
    }
}
