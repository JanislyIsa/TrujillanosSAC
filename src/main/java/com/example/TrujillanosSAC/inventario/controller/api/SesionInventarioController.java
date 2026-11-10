package com.example.TrujillanosSAC.inventario.controller.api;

import com.example.TrujillanosSAC.usuarios.repository.UsuarioRepository;
import com.example.TrujillanosSAC.Service.AdminAccessService;
import com.example.TrujillanosSAC.inventario.exception.InventarioException;
import jakarta.servlet.http.*;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import org.springframework.http.*;
import org.springframework.security.authentication.*;
import org.springframework.security.core.*;
import org.springframework.security.core.context.*;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.security.web.authentication.session.ChangeSessionIdAuthenticationStrategy;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.security.web.csrf.*;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/inventario/sesion")
public class SesionInventarioController {
    private final AuthenticationManager authenticationManager;
    private final AdminAccessService acceso;
    private final UsuarioRepository usuarios;
    private final HttpSessionSecurityContextRepository contextos = new HttpSessionSecurityContextRepository();
    private final CookieCsrfTokenRepository csrf = CookieCsrfTokenRepository.withHttpOnlyFalse();
    public SesionInventarioController(AuthenticationProvider provider, AdminAccessService acceso, UsuarioRepository usuarios) {
        this.authenticationManager = new ProviderManager(provider); this.acceso=acceso; this.usuarios=usuarios;
    }
    public record LoginRequest(@NotBlank @Size(max=20) String username, @NotBlank @Size(max=255) String password) {}
    public record SesionResponse(String username, String nombre, String cargo, boolean inventario, boolean compras) {}
    @PostMapping("/login")
    public SesionResponse login(@Valid @RequestBody LoginRequest datos, HttpServletRequest request, HttpServletResponse response) {
        Authentication auth;
        try { auth=authenticationManager.authenticate(UsernamePasswordAuthenticationToken.unauthenticated(datos.username(),datos.password())); }
        catch (AuthenticationException ex) { throw new InventarioException(HttpStatus.UNAUTHORIZED,"Usuario o contraseña incorrectos"); }
        SesionResponse sesion=sesion(auth);
        if (!sesion.inventario() && !sesion.compras())
            throw new InventarioException(HttpStatus.FORBIDDEN,"Tu cuenta no tiene acceso a inventario o compras");
        new ChangeSessionIdAuthenticationStrategy().onAuthentication(auth,request,response);
        new CsrfAuthenticationStrategy(csrf).onAuthentication(auth,request,response);
        var contexto=SecurityContextHolder.createEmptyContext(); contexto.setAuthentication(auth);
        SecurityContextHolder.setContext(contexto); contextos.saveContext(contexto,request,response);
        return sesion;
    }
    @GetMapping @Transactional(readOnly=true)
    public SesionResponse sesion(Authentication auth) {
        var u=usuarios.findByUsernameConRolesYPersona(auth.getName())
                .orElseThrow(() -> InventarioException.noEncontrado("Usuario"));
        boolean empleado=auth.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_EMPLEADO"));
        String nombre=u.getPersona().getNombre()+" "+u.getPersona().getApellido();
        return new SesionResponse(auth.getName(),nombre,acceso.obtenerCargo(auth.getName()),
                empleado && acceso.puedeAccederA(auth.getName(),"/admin/inventario"),
                empleado && acceso.puedeAccederA(auth.getName(),"/admin/compras/solicitudes"));
    }
    @PostMapping("/logout")
    public ResponseEntity<Void> logout(Authentication auth,HttpServletRequest request,HttpServletResponse response) {
        new SecurityContextLogoutHandler().logout(request,response,auth);
        csrf.saveToken(null,request,response);
        return ResponseEntity.noContent().build();
    }
}
