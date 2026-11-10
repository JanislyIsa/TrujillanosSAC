package com.example.TrujillanosSAC.ventas;

import com.example.TrujillanosSAC.exception.GlobalExceptionHandler;
import com.example.TrujillanosSAC.exception.RecursosNoEncontradosException;
import com.example.TrujillanosSAC.exception.ReglaNegocioException;
import com.example.TrujillanosSAC.inventario.exception.InventarioException;
import com.example.TrujillanosSAC.ventas.controller.MetodoPagoController;
import com.example.TrujillanosSAC.ventas.controller.PagoController;
import com.example.TrujillanosSAC.ventas.controller.PedidoController;
import com.example.TrujillanosSAC.ventas.dto.response.CatalogoResponseDTO;
import com.example.TrujillanosSAC.ventas.dto.response.PedidoResponseDTO;
import com.example.TrujillanosSAC.ventas.exception.VentasExceptionHandler;
import com.example.TrujillanosSAC.ventas.model.codigo.EstadoPedidoCodigo;
import com.example.TrujillanosSAC.ventas.service.ActorVenta;
import com.example.TrujillanosSAC.ventas.service.MetodoPagoService;
import com.example.TrujillanosSAC.ventas.service.PagoService;
import com.example.TrujillanosSAC.ventas.service.PedidoService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.math.BigDecimal;
import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

class VentasControllerTests {

    private static final String PEDIDO = "{\"detalles\":[{\"productoId\":2,\"cantidad\":3}]}";

    @Configuration
    @EnableMethodSecurity
    static class Security {
    }

    private AnnotationConfigApplicationContext context;
    private MockMvc mvc;
    private PedidoService pedidos;
    private MetodoPagoService metodos;
    private PagoService pagos;

    @BeforeEach
    void configurar() {
        pedidos = mock(PedidoService.class);
        metodos = mock(MetodoPagoService.class);
        pagos = mock(PagoService.class);
        context = new AnnotationConfigApplicationContext();
        context.register(Security.class);
        context.registerBean(PedidoController.class, () -> new PedidoController(pedidos));
        context.registerBean(MetodoPagoController.class, () -> new MetodoPagoController(metodos));
        context.registerBean(PagoController.class, () -> new PagoController(pagos));
        context.refresh();
        mvc = MockMvcBuilders.standaloneSetup(context.getBean(PedidoController.class),
                        context.getBean(MetodoPagoController.class), context.getBean(PagoController.class))
                .setControllerAdvice(new VentasExceptionHandler(), new GlobalExceptionHandler()).build();
        autenticar("ROLE_EMPLEADO");
    }

    private void autenticar(String rol) {
        SecurityContextHolder.getContext().setAuthentication(new UsernamePasswordAuthenticationToken(
                "test", "sin-uso", AuthorityUtils.createAuthorityList(rol)));
    }

    @AfterEach
    void cerrar() {
        SecurityContextHolder.clearContext();
        context.close();
    }

    @Test
    void creaPedidoCon201YPasaQuienLoHace() throws Exception {
        when(pedidos.crear(any(), any())).thenReturn(new PedidoResponseDTO(7L, null, 1L, null, BigDecimal.TEN, List.of()));
        autenticar("ROLE_CLIENTE");
        mvc.perform(post("/api/v1/ventas/pedidos").contentType("application/json").content(PEDIDO))
                .andExpect(status().isCreated()).andExpect(jsonPath("$.idPedido").value(7));
        verify(pedidos).crear(argThat(r -> r.detalles().get(0).cantidad() == 3), eq(new ActorVenta("test", false)));
    }

    @Test
    void elEmpleadoNoCreaPedidos() throws Exception {
        mvc.perform(post("/api/v1/ventas/pedidos").contentType("application/json").content(PEDIDO))
                .andExpect(status().isForbidden());
        verifyNoInteractions(pedidos);
    }

    @Test
    void validaElDetalleAntesDeLlamarAlServicio() throws Exception {
        autenticar("ROLE_CLIENTE");
        mvc.perform(post("/api/v1/ventas/pedidos").contentType("application/json")
                        .content("{\"detalles\":[{\"productoId\":2,\"cantidad\":0}]}"))
                .andExpect(status().isBadRequest());
        verifyNoInteractions(pedidos);
    }

    @Test
    void clienteListaSoloLoSuyoYProveedorNoEntra() throws Exception {
        autenticar("ROLE_CLIENTE");
        mvc.perform(get("/api/v1/ventas/pedidos")).andExpect(status().isOk());
        verify(pedidos).listar(new ActorVenta("test", false));

        autenticar("ROLE_PROVEEDOR");
        mvc.perform(get("/api/v1/ventas/pedidos")).andExpect(status().isForbidden());
        verifyNoMoreInteractions(pedidos);
    }

    @Test
    void erroresDelServicioSeRespondenConSuCodigo() throws Exception {
        when(pedidos.obtener(eq(99L), any())).thenThrow(new RecursosNoEncontradosException("No existe"));
        mvc.perform(get("/api/v1/ventas/pedidos/99")).andExpect(status().isNotFound());

        when(pedidos.crear(any(), any())).thenThrow(new ReglaNegocioException("Stock insuficiente"));
        autenticar("ROLE_CLIENTE");
        mvc.perform(post("/api/v1/ventas/pedidos").contentType("application/json").content(PEDIDO))
                .andExpect(status().isConflict());
    }

    @Test
    void conflictoDeInventarioSeRespondeComo409() throws Exception {
        when(pedidos.crear(any(), any())).thenThrow(InventarioException.conflicto("La salida supera el stock disponible"));
        autenticar("ROLE_CLIENTE");
        mvc.perform(post("/api/v1/ventas/pedidos").contentType("application/json").content(PEDIDO))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.message").value("La salida supera el stock disponible"));
    }

    @Test
    void soloUnEmpleadoAvanzaElEstado() throws Exception {
        String cuerpo = "{\"estado\":\"ENTREGADO\"}";
        autenticar("ROLE_CLIENTE");
        mvc.perform(patch("/api/v1/ventas/pedidos/5/estado").contentType("application/json").content(cuerpo))
                .andExpect(status().isForbidden());
        autenticar("ROLE_EMPLEADO");
        mvc.perform(patch("/api/v1/ventas/pedidos/5/estado").contentType("application/json").content(cuerpo))
                .andExpect(status().isOk());
        verify(pedidos).cambiarEstado(5L, EstadoPedidoCodigo.ENTREGADO);
    }

    @Test
    void pagoConCulqiExigeTokenYCorreo() throws Exception {
        autenticar("ROLE_CLIENTE");
        mvc.perform(post("/api/v1/ventas/pagos/culqi").contentType("application/json")
                        .content("{\"pedidoId\":5,\"email\":\"no-es-correo\"}"))
                .andExpect(status().isBadRequest());
        verifyNoInteractions(pagos);
    }

    @Test
    void soloElClientePaga() throws Exception {
        mvc.perform(post("/api/v1/ventas/pagos/culqi").contentType("application/json")
                        .content("{\"pedidoId\":5,\"token\":\"tkn_test\",\"email\":\"ana@example.test\"}"))
                .andExpect(status().isForbidden());
        verifyNoInteractions(pagos);
    }

    @Test
    void catalogoSeConsultaPeroSoloElEmpleadoLoRenombra() throws Exception {
        String cuerpo = "{\"nombre\":\"Tarjeta de crédito o débito\"}";
        when(metodos.actualizar(eq(1L), any())).thenReturn(new CatalogoResponseDTO(1L, "TARJETA", "Tarjeta de crédito o débito"));
        mvc.perform(put("/api/v1/ventas/metodos-pago/1").contentType("application/json").content(cuerpo))
                .andExpect(status().isOk()).andExpect(jsonPath("$.codigo").value("TARJETA"));

        autenticar("ROLE_CLIENTE");
        mvc.perform(put("/api/v1/ventas/metodos-pago/1").contentType("application/json").content(cuerpo))
                .andExpect(status().isForbidden());
        mvc.perform(get("/api/v1/ventas/metodos-pago")).andExpect(status().isOk());
        mvc.perform(delete("/api/v1/ventas/metodos-pago/1")).andExpect(status().isMethodNotAllowed());
    }

    @Test
    void noExponeBorradoDePedidos() throws Exception {
        mvc.perform(delete("/api/v1/ventas/pedidos/1")).andExpect(status().isMethodNotAllowed());
        verifyNoInteractions(pedidos);
    }
}
