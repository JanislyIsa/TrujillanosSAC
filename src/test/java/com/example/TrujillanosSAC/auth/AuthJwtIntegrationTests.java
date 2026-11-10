package com.example.TrujillanosSAC.auth;

import com.example.TrujillanosSAC.Model.Cliente;
import com.example.TrujillanosSAC.Model.Persona;
import com.example.TrujillanosSAC.Model.Rol;
import com.example.TrujillanosSAC.Model.TipoDocumento;
import com.example.TrujillanosSAC.Model.Usuario;
import com.example.TrujillanosSAC.Repository.RolRepository;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.not;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/** La API de ventas con JWT: login, 401 (sin token, vencido, inválido, clave incorrecta) y 403 por rol. */
@ActiveProfiles("test")
@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class AuthJwtIntegrationTests {

    private static final String CLAVE = "Clave2026!";

    @Autowired MockMvc mvc;
    @Autowired EntityManager em;
    @Autowired PasswordEncoder passwordEncoder;
    @Autowired RolRepository roles;
    @Autowired JwtService jwtService;

    private String clienteUser;

    @BeforeEach
    void crearCliente() {
        String sufijo = UUID.randomUUID().toString().substring(0, 8);
        clienteUser = "cli-" + sufijo;
        TipoDocumento dni = TipoDocumento.builder().nombre("DNI-" + sufijo).abreviatura(sufijo).longitud(8).build();
        em.persist(dni);
        Persona persona = Persona.builder().nombre("Ana").apellido("Prueba").telefono("999999999")
                .documento(dni).numDoc(sufijo).build();
        em.persist(persona);
        em.persist(Cliente.builder().persona(persona).fechaRegistro(LocalDate.now()).build());
        Rol rolCliente = roles.findByNombreIgnoreCase("CLIENTE").orElseGet(() -> {
            Rol r = Rol.builder().nombre("CLIENTE").build();
            em.persist(r);
            return r;
        });
        em.persist(Usuario.builder().username(clienteUser).password(passwordEncoder.encode(CLAVE))
                .persona(persona).roles(new HashSet<>(Set.of(rolCliente))).build());
        em.flush();
    }

    private String login(String usuario, String clave) {
        return "{\"username\":\"" + usuario + "\",\"password\":\"" + clave + "\"}";
    }

    @Test
    void loginCorrectoDevuelveTokenRolesYPermisos() throws Exception {
        mvc.perform(post("/api/v1/auth/login").contentType("application/json").content(login(clienteUser, CLAVE)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").isNotEmpty())
                .andExpect(jsonPath("$.tipo").value("Bearer"))
                .andExpect(jsonPath("$.expiraEnSegundos").value(3600))
                .andExpect(jsonPath("$.username").value(clienteUser))
                .andExpect(jsonPath("$.nombreCompleto").value("Ana Prueba"))
                .andExpect(jsonPath("$.roles[0]").value("CLIENTE"))
                .andExpect(jsonPath("$.permisos", hasItem("PEDIDO_CREAR")))
                .andExpect(jsonPath("$.permisos", not(hasItem("PEDIDO_CAMBIAR_ESTADO"))));
    }

    @Test
    void loginConClaveIncorrectaDa401() throws Exception {
        mvc.perform(post("/api/v1/auth/login").contentType("application/json").content(login(clienteUser, "otra")))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.status").value(401))
                .andExpect(jsonPath("$.message").value("Usuario o contraseña incorrectos"));
        mvc.perform(post("/api/v1/auth/login").contentType("application/json").content(login("no-existe", CLAVE)))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.message").value("Usuario o contraseña incorrectos"));
    }

    @Test
    void peticionSinTokenDa401EnJson() throws Exception {
        mvc.perform(get("/api/v1/ventas/pedidos"))
                .andExpect(status().isUnauthorized())
                .andExpect(header().string("WWW-Authenticate", "Bearer"))
                .andExpect(jsonPath("$.status").value(401))
                .andExpect(jsonPath("$.path").value("/api/v1/ventas/pedidos"));
    }

    @Test
    void tokenVencidoOInvalidoDa401() throws Exception {
        String vencido = jwtService.generarToken(clienteUser, List.of("CLIENTE"), Instant.now().minus(2, ChronoUnit.HOURS));
        mvc.perform(get("/api/v1/ventas/pedidos").header("Authorization", "Bearer " + vencido))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.message", containsString("expirado")));
        mvc.perform(get("/api/v1/ventas/pedidos").header("Authorization", "Bearer abc.def.ghi"))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.message").value("El token no es válido"));
    }

    @Test
    void clienteConTokenEntraASusPedidosPeroNoAEndpointsDeEmpleado() throws Exception {
        String token = "Bearer " + jwtService.generarToken(clienteUser, List.of("CLIENTE"));
        mvc.perform(get("/api/v1/ventas/pedidos").header("Authorization", token))
                .andExpect(status().isOk());
        mvc.perform(get("/api/v1/ventas/comprobantes").header("Authorization", token))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.status").value(403));
    }

    @Test
    void noUsaSesionNiCsrf() throws Exception {
        String token = "Bearer " + jwtService.generarToken(clienteUser, List.of("CLIENTE"));
        // POST sin token CSRF: antes daba 403; ahora la validación del cuerpo responde 400.
        mvc.perform(post("/api/v1/ventas/pedidos").header("Authorization", token)
                        .contentType("application/json").content("{\"detalles\":[]}"))
                .andExpect(status().isBadRequest())
                .andExpect(header().doesNotExist("Set-Cookie"));
    }

    @Test
    void permiteElPreflightCorsDelFrontend() throws Exception {
        mvc.perform(options("/api/v1/auth/login")
                        .header("Origin", "http://localhost:4200")
                        .header("Access-Control-Request-Method", "POST")
                        .header("Access-Control-Request-Headers", "Content-Type"))
                .andExpect(status().isOk())
                .andExpect(header().string("Access-Control-Allow-Origin", "http://localhost:4200"));
    }
}
