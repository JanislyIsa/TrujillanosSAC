package com.example.TrujillanosSAC.auth;

import java.util.ArrayList;
import java.util.Collection;
import java.util.EnumSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

/**
 * Permisos de cada rol (basado en sysventas). Deben coincidir con los @PreAuthorize de los controllers:
 * estos permisos solo informan al frontend; quien decide el acceso es el backend.
 */
public final class PermisosPorRol {

    private static final Map<String, Set<Permiso>> PERMISOS = Map.of(
            "CLIENTE", EnumSet.of(
                    Permiso.PEDIDO_CREAR,
                    Permiso.PEDIDO_VER_PROPIOS,
                    Permiso.PEDIDO_CANCELAR,
                    Permiso.PAGO_REALIZAR,
                    Permiso.COMPROBANTE_EMITIR),
            "EMPLEADO", EnumSet.of(
                    Permiso.PEDIDO_VER_TODOS,
                    Permiso.PEDIDO_CANCELAR,
                    Permiso.PEDIDO_CAMBIAR_ESTADO,
                    Permiso.PAGO_VER_TODOS,
                    Permiso.COMPROBANTE_EMITIR,
                    Permiso.COMPROBANTE_VER_TODOS,
                    Permiso.CATALOGO_EDITAR));

    private PermisosPorRol() {
    }

    /** Unión de los permisos de todos los roles (nombres sin "ROLE_"), en orden alfabético. */
    public static List<String> permisos(Collection<String> roles) {
        Set<String> nombres = new TreeSet<>();
        roles.forEach(rol -> PERMISOS.getOrDefault(rol, Set.of())
                .forEach(permiso -> nombres.add(permiso.name())));
        return new ArrayList<>(nombres);
    }
}
