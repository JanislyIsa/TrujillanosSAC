package com.example.TrujillanosSAC.auth.dto;

import java.util.List;

/** El frontend guarda el token y lo envía en cada petición como: Authorization: Bearer &lt;token&gt; */
public record LoginResponseDTO(
        String token,
        String tipo,
        long expiraEnSegundos,
        String username,
        String nombreCompleto,
        /** Correo del cliente; null si no tiene (por ejemplo empleados o clientes creados por un empleado). */
        String correo,
        /** Número de documento (DNI, RUC o CE) de la persona; el comprobante lo muestra. */
        String numeroDocumento,
        List<String> roles,
        List<String> permisos,
        /** Cargo del empleado (ALMACENERO, JEFE_DE_COMPRAS...); null si no es empleado. */
        String cargo,
        /** El empleado puede entrar al módulo de inventario (según su cargo). */
        boolean inventario,
        /** El empleado puede entrar al módulo de compras (según su cargo). */
        boolean compras) {
}
