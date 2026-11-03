package com.example.TrujillanosSAC.ServiceImpl;

import com.example.TrujillanosSAC.Dto.AdminModuleDto;
import com.example.TrujillanosSAC.Model.Empleado;
import com.example.TrujillanosSAC.Model.Rol;
import com.example.TrujillanosSAC.Model.Usuario;
import com.example.TrujillanosSAC.Repository.EmpleadoRepository;
import com.example.TrujillanosSAC.Repository.UsuarioRepository;
import com.example.TrujillanosSAC.Service.AdminAccessService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.text.Normalizer;
import java.util.List;
import java.util.Locale;

@Service
public class AdminAccessServiceImpl implements AdminAccessService {

    private final UsuarioRepository usuarioRepository;
    private final EmpleadoRepository empleadoRepository;

    public AdminAccessServiceImpl(UsuarioRepository usuarioRepository,
                                  EmpleadoRepository empleadoRepository) {
        this.usuarioRepository = usuarioRepository;
        this.empleadoRepository = empleadoRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public boolean puedeAbrirPanel(String username) {
        return obtenerCargoNormalizado(username) != null;
    }

    @Override
    @Transactional(readOnly = true)
    public String obtenerCargo(String username) {
        return obtenerEmpleado(username)
                .map(Empleado::getCargo)
                .map(cargo -> cargo.getNombre())
                .orElse("");
    }

    @Override
    @Transactional(readOnly = true)
    public List<AdminModuleDto> obtenerModulos(String username) {
        String cargo = obtenerCargoNormalizado(username);

        if (cargo == null) {
            return List.of();
        }

        return switch (cargo) {
            case "ADMIN", "ADMINISTRADOR" -> List.of(
                    new AdminModuleDto("Dashboard", "Resumen general del negocio", "fa-table-columns", "/admin/dashboard"),
                    new AdminModuleDto("Inventario", "Stock, almacenes y productos", "fa-boxes-stacked", "/admin/inventario"),
                    new AdminModuleDto("Compras", "Solicitudes, proveedores y ordenes", "fa-clipboard-list", "/admin/compras/solicitudes"),
                    new AdminModuleDto("Ventas", "Pedidos, clientes y comprobantes", "fa-chart-line", "/ventas/catalogo"),
                    new AdminModuleDto("Reportes", "Indicadores y movimientos", "fa-chart-pie", "/admin/reportes")
            );
            case "JEFE_COMPRAS", "JEFE_DE_COMPRAS" -> List.of(
                    new AdminModuleDto("Compras", "Solicitudes y ordenes de compra", "fa-clipboard-list", "/admin/compras/solicitudes"),
                    new AdminModuleDto("Proveedores", "Gestion de proveedores", "fa-truck-field", "/admin/proveedores")
            );
            case "JEFE_VENTAS", "JEFE_DE_VENTAS" -> List.of(
                    new AdminModuleDto("Ventas", "Pedidos y comprobantes", "fa-chart-line", "/ventas/catalogo"),
                    new AdminModuleDto("Clientes", "Gestion de clientes", "fa-users", "/admin/clientes")
            );
            case "ALMACENERO" -> List.of(
                    new AdminModuleDto("Inventario", "Stock y movimientos de almacen", "fa-boxes-stacked", "/admin/inventario")
            );
            default -> List.of();
        };
    }

    @Override
    @Transactional(readOnly = true)
    public boolean puedeAccederA(String username, String url) {
        return obtenerModulos(username).stream()
                .map(AdminModuleDto::url)
                .anyMatch(moduloUrl -> url.equals(moduloUrl)
                        || url.startsWith(moduloUrl + "/")
                        || moduloUrl.startsWith(url + "/"));
    }

    private String obtenerCargoNormalizado(String username) {
        return obtenerEmpleado(username)
                .map(Empleado::getCargo)
                .map(cargo -> normalizar(cargo.getNombre()))
                .orElse(null);
    }

    private java.util.Optional<Empleado> obtenerEmpleado(String username) {
        return usuarioRepository.findByUsernameConRolesYPersona(username)
                .filter(this::esEmpleado)
                .flatMap(usuario -> empleadoRepository.findByIdPersonaConCargo(usuario.getPersona().getIdPersona()));
    }

    private boolean esEmpleado(Usuario usuario) {
        return usuario.getRoles().stream()
                .map(Rol::getNombre)
                .map(this::normalizar)
                .anyMatch(rol -> "EMPLEADO".equals(rol) || "ROLE_EMPLEADO".equals(rol));
    }

    private String normalizar(String valor) {
        String sinTildes = Normalizer.normalize(valor, Normalizer.Form.NFD)
                .replaceAll("\\p{M}", "");
        return sinTildes.trim()
                .toUpperCase(Locale.ROOT)
                .replace(' ', '_');
    }
}
