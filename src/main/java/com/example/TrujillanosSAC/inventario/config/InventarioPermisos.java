package com.example.TrujillanosSAC.inventario.config;
import com.example.TrujillanosSAC.Service.AdminAccessService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

@Component("inventarioPermisos") @RequiredArgsConstructor
public class InventarioPermisos {
    private final AdminAccessService acceso;
    private boolean empleado(Authentication auth) {
        return auth != null && auth.isAuthenticated() && auth.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_EMPLEADO"));
    }
    public boolean inventario(Authentication auth) {
        return empleado(auth) && acceso.puedeAccederA(auth.getName(),"/admin/inventario");
    }
    public boolean compras(Authentication auth) {
        return empleado(auth) && acceso.puedeAccederA(auth.getName(),"/admin/compras/solicitudes");
    }
    public boolean lectura(Authentication auth) { return inventario(auth) || compras(auth); }
}
