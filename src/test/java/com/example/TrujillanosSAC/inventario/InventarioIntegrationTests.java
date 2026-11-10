package com.example.TrujillanosSAC.inventario;

import com.example.TrujillanosSAC.Model.*;
import com.example.TrujillanosSAC.Repository.*;
import com.example.TrujillanosSAC.exception.ReglaNegocioException;
import com.example.TrujillanosSAC.ventas.dto.request.DetallePedidoRequestDTO;
import com.example.TrujillanosSAC.ventas.dto.request.PedidoRequestDTO;
import com.example.TrujillanosSAC.ventas.service.ActorVenta;
import com.example.TrujillanosSAC.ventas.service.PedidoService;
import com.example.TrujillanosSAC.inventario.dto.InventarioDtos.*;
import com.example.TrujillanosSAC.inventario.exception.InventarioException;
import com.example.TrujillanosSAC.inventario.model.*;
import com.example.TrujillanosSAC.inventario.repository.*;
import com.example.TrujillanosSAC.inventario.service.*;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.*;
import org.springframework.transaction.support.TransactionTemplate;
import java.math.BigDecimal;
import java.time.*;
import java.util.*;
import java.util.concurrent.*;
import static org.assertj.core.api.Assertions.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ActiveProfiles("test")
@SpringBootTest @AutoConfigureMockMvc @Transactional
class InventarioIntegrationTests {
    @Autowired EntityManager em;
    @Autowired PlatformTransactionManager transactions;
    @Autowired CatalogoInventarioService catalogo;
    @Autowired MovimientoInventarioService movimientos;
    @Autowired AlertaInventarioService alertas;
    @Autowired SolicitudInventarioService solicitudes;
    @Autowired PedidoService pedidos;
    @Autowired ProductoRepository productos;
    @Autowired MovimientoRepository movimientoRepository;
    @Autowired ComprobanteMovimientoRepository comprobanteRepository;
    @Autowired SolicitudCompraRepository solicitudRepository;
    @Autowired DetalleSolicitudRepository detalleSolicitudRepository;
    @Autowired RolRepository roles;
    @Autowired MockMvc mvc;
    @Autowired org.springframework.security.crypto.password.PasswordEncoder passwordEncoder;
    @Autowired UsuarioRepository usuarios;
    Long productoId, insumoId, proveedorId, almacenId, categoriaId, presentacionId, unidadId;
    String almacenUser, jefeUser, clienteUser;
    TipoDocumento documento;

    @BeforeEach
    void preparar() {
        new TransactionTemplate(transactions).executeWithoutResult(tx -> {
            String sufijo = UUID.randomUUID().toString().substring(0,8);
            almacenUser="alm-"+sufijo; jefeUser="jefe-"+sufijo; clienteUser="cli-"+sufijo;
            documento = TipoDocumento.builder().nombre("DNI-"+sufijo).abreviatura(sufijo).longitud(8).build(); em.persist(documento);
            Rol rol = roles.findByNombreIgnoreCase("EMPLEADO").orElseGet(() -> {
                Rol r=Rol.builder().nombre("EMPLEADO").build(); em.persist(r); return r;
            });
            Empleado almacenero = empleado("ALMACENERO",almacenUser,rol);
            empleado("Jefe de Compras",jefeUser,rol);
            Almacen almacen = Almacen.builder().nombre("Principal "+sufijo).empleadoResponsable(almacenero).build(); em.persist(almacen);
            almacenId=almacen.getIdAlmacen();
            Categoria categoria = Categoria.builder().nombre("Dulces "+sufijo).build(); em.persist(categoria); categoriaId=categoria.getIdCategoria();
            PresentacionProducto presentacion=PresentacionProducto.builder().nombre("Bolsa").build(); em.persist(presentacion); presentacionId=presentacion.getIdPresentacion();
            UnidadMedida unidad=UnidadMedida.builder().nombre("Kilogramo").build(); em.persist(unidad); unidadId=unidad.getIdUnidadMedida();
            Proveedor proveedor=Proveedor.builder().persona(persona("Proveedor")).correo(sufijo+"@example.test").direccion("Trujillo").build(); em.persist(proveedor); proveedorId=proveedor.getIdPersona();
            if (em.createQuery("select count(e) from EstadoSolicitudCompra e where lower(e.nombre)='pendiente'",Long.class).getSingleResult()==0)
                em.persist(EstadoSolicitudCompra.builder().nombre("Pendiente").build());
            Persona pc=persona("Cliente");
            em.persist(Cliente.builder().persona(pc).fechaRegistro(LocalDate.now()).build());
            em.persist(Usuario.builder().username(clienteUser).password("hash-test").persona(pc).build());
            for (String codigo : List.of("PENDIENTE","CANCELADO")) {
                if (em.createQuery("select count(e) from VentaEstadoPedido e where e.codigo=:c",Long.class)
                        .setParameter("c",codigo).getSingleResult()==0) {
                    var estado=new com.example.TrujillanosSAC.ventas.model.EstadoPedido();
                    estado.setCodigo(codigo); estado.setNombre(codigo); em.persist(estado);
                }
            }
            productoId=catalogo.crearProducto(producto(10,5,20,"Caramelo"),almacenUser).id();
            insumoId=catalogo.crearInsumo(new InsumoRequest("Azucar",3,5,20,unidadId,almacenId),almacenUser).id();
            em.flush();
        });
    }

    private Persona persona(String nombre) {
        Persona p=Persona.builder().nombre(nombre).apellido("Prueba").telefono("999999999")
                .documento(documento).numDoc(UUID.randomUUID().toString().substring(0,8)).build(); em.persist(p); return p;
    }
    private Empleado empleado(String cargoNombre,String username,Rol rol) {
        Cargo cargo=em.createQuery("select c from Cargo c where c.nombre=:nombre",Cargo.class)
                .setParameter("nombre",cargoNombre).getResultStream().findFirst().orElse(null);
        if (cargo==null) { cargo=Cargo.builder().nombre(cargoNombre).build(); em.persist(cargo); }
        Persona persona=persona(username);
        Empleado e=Empleado.builder().persona(persona).cargo(cargo).salario(BigDecimal.valueOf(1500))
                .fechaContratacion(LocalDate.now()).build(); em.persist(e);
        em.persist(Usuario.builder().username(username).password("hash-test").persona(persona).roles(new HashSet<>(Set.of(rol))).build());
        return e;
    }
    private ProductoRequest producto(Integer stock,int minimo,int limite,String nombre) {
        return new ProductoRequest(nombre,"Producto de prueba",BigDecimal.valueOf(12.50),stock,minimo,limite,
                categoriaId,presentacionId,almacenId,null);
    }
    private MovimientoRequest salida(int cantidad) {
        return new MovimientoRequest(TipoMovimiento.SALIDA,TipoItem.PRODUCTO,productoId,cantidad,"Despacho de prueba");
    }

    @Test void creaSaldoInicialYComprobantePersistente() {
        var historial=movimientos.listar(TipoItem.PRODUCTO,productoId,null,null,null,null);
        assertThat(historial).hasSize(1);
        assertThat(historial.getFirst().stockAnterior()).isZero();
        assertThat(historial.getFirst().stockNuevo()).isEqualTo(10);
        var resultado=movimientos.registrar(salida(4),almacenUser);
        assertThat(resultado.movimiento().stockAnterior()).isEqualTo(10);
        assertThat(resultado.movimiento().stockNuevo()).isEqualTo(6);
        catalogo.editarProducto(productoId,producto(null,5,20,"Nombre nuevo"));
        em.flush(); em.clear();
        var comprobante=movimientos.comprobante(resultado.movimiento().idMovimiento());
        assertThat(comprobanteRepository.findById(resultado.movimiento().idMovimiento())).isPresent();
        long totalComprobantes=comprobanteRepository.count();
        movimientos.comprobante(resultado.movimiento().idMovimiento());
        assertThat(comprobanteRepository.count()).isEqualTo(totalComprobantes);
        assertThat(comprobante.movimiento().nombreItem()).isEqualTo("Caramelo");
        assertThat(comprobante.movimiento().stockNuevo()).isEqualTo(6);
        assertThat(comprobante.responsable()).isEqualTo(almacenUser);
    }
    @Test void salidaInvalidaNoGuardaMovimientoNiCambiaSaldo() {
        long antes=movimientoRepository.count();
        long comprobantesAntes=comprobanteRepository.count();
        assertThatThrownBy(() -> movimientos.registrar(salida(11),almacenUser)).isInstanceOf(InventarioException.class);
        assertThat(productos.findById(productoId).orElseThrow().getStock()).isEqualTo(10);
        assertThat(movimientoRepository.count()).isEqualTo(antes);
        assertThat(comprobanteRepository.count()).isEqualTo(comprobantesAntes);
    }
    @Test void rechazaCantidadesYLimitesInvalidos() {
        assertThatThrownBy(() -> movimientos.registrar(salida(0),almacenUser)).isInstanceOf(jakarta.validation.ConstraintViolationException.class);
        assertThatThrownBy(() -> catalogo.crearProducto(producto(0,20,5,"Invalido"),almacenUser)).isInstanceOf(IllegalArgumentException.class);
        assertThatThrownBy(() -> catalogo.editarProducto(productoId,producto(999,5,20,"Caramelo"))).isInstanceOf(InventarioException.class);
    }
    @Test void alertasSeDeduplicanResuelvenYNotificanAlJefe() {
        alertas.sincronizar(TipoItem.INSUMO,insumoId);
        alertas.sincronizar(TipoItem.INSUMO,insumoId);
        assertThat(alertas.listar(true).stream().filter(a -> a.idItem().equals(insumoId) && a.tipoItem()==TipoItem.INSUMO)).hasSize(1);
        var notificaciones=alertas.notificaciones(jefeUser);
        assertThat(notificaciones).hasSize(1);
        alertas.marcarLeida(notificaciones.getFirst().id(),jefeUser);
        assertThat(alertas.notificaciones(jefeUser).getFirst().leidaEn()).isNotNull();
        movimientos.registrar(new MovimientoRequest(TipoMovimiento.INGRESO,TipoItem.INSUMO,insumoId,4,"Reposicion"),almacenUser);
        assertThat(alertas.listar(true).stream().filter(a -> a.idItem().equals(insumoId) && a.tipoItem()==TipoItem.INSUMO)).isEmpty();
        assertThat(alertas.notificaciones(jefeUser).getFirst().alerta().resueltaEn()).isNotNull();
        movimientos.registrar(new MovimientoRequest(TipoMovimiento.INGRESO,TipoItem.INSUMO,insumoId,20,"Ingreso adicional"),almacenUser);
        assertThat(alertas.notificaciones(jefeUser)).hasSize(2);
        assertThat(alertas.listar(true).stream().anyMatch(a -> a.tipoItem()==TipoItem.INSUMO && a.idItem().equals(insumoId) && a.tipoAlerta().equals("SOBRESTOCK"))).isTrue();
    }
    @Test void notificacionSoloPuedeMarcarlaSuDestinatario() {
        var id=alertas.notificaciones(jefeUser).getFirst().id();
        assertThatThrownBy(() -> alertas.marcarLeida(id,almacenUser)).isInstanceOf(InventarioException.class);
    }
    @Test void solicitudesAutomaticasLleganAlModuloComprasConSusDetalles() {
        var s=solicitudes.automatica(proveedorId,jefeUser);
        assertThat(s.estado()).isEqualTo("Pendiente");
        assertThat(s.detalles()).anySatisfy(d -> {
            assertThat(d.idInsumo()).isEqualTo(insumoId);
            assertThat(d.cantidad()).isEqualByComparingTo("17");
        });
        assertThat(solicitudRepository.findAllParaProveedor()).anyMatch(x -> x.getIdSolicitud().equals(s.id()));
        assertThat(detalleSolicitudRepository.findBySolicitudParaProveedor(s.id())).isNotEmpty();
    }
    @Test void solicitudConDetalleInexistenteEsAtomica() {
        long antes=solicitudRepository.count();
        assertThatThrownBy(() -> solicitudes.crear(new SolicitudRequest(proveedorId,List.of(
                new SolicitudLineaRequest(insumoId,BigDecimal.ONE),
                new SolicitudLineaRequest(Long.MAX_VALUE,BigDecimal.ONE))),jefeUser)).isInstanceOf(InventarioException.class);
        assertThat(solicitudRepository.count()).isEqualTo(antes);
    }
    @Test void eliminarInactivaConservandoSaldoEHistorial() {
        Long id=catalogo.crearProducto(producto(0,0,10,"Temporal"),almacenUser).id();
        catalogo.eliminar(TipoItem.PRODUCTO,id);
        assertThat(productos.findById(id).orElseThrow().getActivo()).isFalse();
        catalogo.eliminar(TipoItem.PRODUCTO,productoId);
        assertThat(catalogo.obtener(TipoItem.PRODUCTO,productoId).activo()).isFalse();
        assertThat(catalogo.obtener(TipoItem.PRODUCTO,productoId).stock()).isEqualTo(10);
    }
    @Test void buscarYFiltrarStockYPaginacion() {
        var items=catalogo.listar(TipoItem.INSUMO,"AZU",almacenId,EstadoStock.BAJO);
        assertThat(items).hasSize(1);
        assertThat(items.getFirst().id()).isEqualTo(insumoId);
        assertThat(PaginaResponse.de(items,10,20).contenido()).isEmpty();
        assertThatThrownBy(() -> PaginaResponse.de(items,0,101)).isInstanceOf(IllegalArgumentException.class);
    }
    @Test void ventasAgrupanDuplicadosYRegistranLaSalida() {
        var pedido=pedidos.crear(new PedidoRequestDTO(List.of(
                new DetallePedidoRequestDTO(productoId,2),new DetallePedidoRequestDTO(productoId,3))),
                new ActorVenta(clienteUser,false));
        assertThat(catalogo.obtener(TipoItem.PRODUCTO,productoId).stock()).isEqualTo(5);
        assertThat(movimientos.listar(TipoItem.PRODUCTO,productoId,TipoMovimiento.SALIDA,null,null,null))
                .hasSize(1).allSatisfy(m -> assertThat(m.cantidad()).isEqualTo(5));
        assertThat(pedido.detalles()).hasSize(1);
    }
    @Test void apiValidaJsonAutenticacionPermisosYCrsf() throws Exception {
        mvc.perform(get("/api/v1/inventario/productos")).andExpect(status().isUnauthorized());
        mvc.perform(get("/api/v1/inventario/productos").with(user(clienteUser).roles("CLIENTE"))).andExpect(status().isForbidden());
        mvc.perform(get("/api/v1/inventario/productos").with(user(almacenUser).roles("EMPLEADO")))
                .andExpect(status().isOk()).andExpect(jsonPath("$.contenido").isArray());
        mvc.perform(post("/api/v1/inventario/movimientos").with(user(almacenUser).roles("EMPLEADO"))
                .contentType("application/json").content("{}")).andExpect(status().isForbidden());
        mvc.perform(post("/api/v1/inventario/movimientos").with(user(almacenUser).roles("EMPLEADO")).with(csrf())
                .contentType("application/json").content("{}")).andExpect(status().isBadRequest()).andExpect(jsonPath("$.errores.tipoItem").exists());
        mvc.perform(get("/api/v1/inventario/productos/999999999").with(user(almacenUser).roles("EMPLEADO")))
                .andExpect(status().isNotFound());
        mvc.perform(get("/api/v1/inventario/movimientos?tipoMovimiento=AJUSTE").with(user(almacenUser).roles("EMPLEADO")))
                .andExpect(status().isBadRequest());
    }
    @Test void jefeComprasPuedeSolicitarPeroAlmaceneroNo() throws Exception {
        mvc.perform(get("/api/v1/inventario/solicitudes-compra/faltantes").with(user(almacenUser).roles("EMPLEADO")))
                .andExpect(status().isForbidden());
        mvc.perform(get("/api/v1/inventario/solicitudes-compra/faltantes").with(user(jefeUser).roles("EMPLEADO")))
                .andExpect(status().isOk()).andExpect(jsonPath("$.contenido").isArray());
    }
    @Test void apiRegistraMovimientoYPermiteRecuperarSuComprobante() throws Exception {
        String cuerpo="{\"tipoMovimiento\":\"SALIDA\",\"tipoItem\":\"PRODUCTO\",\"idItem\":"+productoId+
                ",\"cantidad\":2,\"motivo\":\"Despacho API\"}";
        var respuesta=mvc.perform(post("/api/v1/inventario/movimientos")
                .with(user(almacenUser).roles("EMPLEADO")).with(csrf())
                .contentType("application/json").content(cuerpo))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.movimiento.stockAnterior").value(10))
                .andExpect(jsonPath("$.movimiento.stockNuevo").value(8)).andReturn();
        mvc.perform(get(respuesta.getResponse().getHeader("Location")).with(user(almacenUser).roles("EMPLEADO")))
                .andExpect(status().isOk()).andExpect(jsonPath("$.motivo").value("Despacho API"));
        mvc.perform(post("/api/v1/inventario/movimientos").with(user(almacenUser).roles("EMPLEADO")).with(csrf())
                .contentType("application/json").content(cuerpo.replace("\"cantidad\":2","\"cantidad\":99")))
                .andExpect(status().isConflict()).andExpect(jsonPath("$.mensaje").value("La salida supera el stock disponible"));
    }
    @Test void cancelarDosVecesDevuelveStockUnaSolaVez() {
        var cliente=new ActorVenta(clienteUser,false);
        var pedido=pedidos.crear(new PedidoRequestDTO(List.of(new DetallePedidoRequestDTO(productoId,4))),cliente);
        assertThat(catalogo.obtener(TipoItem.PRODUCTO,productoId).stock()).isEqualTo(6);
        pedidos.cancelar(pedido.idPedido(),cliente);
        assertThatThrownBy(() -> pedidos.cancelar(pedido.idPedido(),cliente)).isInstanceOf(ReglaNegocioException.class);
        assertThat(catalogo.obtener(TipoItem.PRODUCTO,productoId).stock()).isEqualTo(10);
        assertThat(movimientos.listar(TipoItem.PRODUCTO,productoId,TipoMovimiento.INGRESO,null,null,null)).hasSize(2);
    }
    @Test void loginMantieneLaSesionYLogoutLaInvalida() throws Exception {
        var usuario=usuarios.findByUsernameConRolesYPersona(almacenUser).orElseThrow();
        usuario.setPassword(passwordEncoder.encode("PruebaInventario1!"));em.flush();
        var respuesta=mvc.perform(post("/api/v1/inventario/sesion/login").with(csrf()).contentType("application/json")
                .content("{\"username\":\""+almacenUser+"\",\"password\":\"PruebaInventario1!\"}"))
                .andExpect(status().isOk()).andExpect(jsonPath("$.inventario").value(true))
                .andExpect(jsonPath("$.compras").value(false)).andReturn();
        var sesion=(org.springframework.mock.web.MockHttpSession)respuesta.getRequest().getSession(false);
        assertThat(sesion).isNotNull();
        mvc.perform(get("/api/v1/inventario/sesion").session(sesion)).andExpect(status().isOk())
                .andExpect(jsonPath("$.username").value(almacenUser));
        mvc.perform(post("/api/v1/inventario/sesion/logout").session(sesion).with(csrf())).andExpect(status().isNoContent());
        mvc.perform(get("/api/v1/inventario/sesion")).andExpect(status().isUnauthorized());
    }
    @Test void frontendAbreSinSesionYPermiteRecargarLaRutaBase() throws Exception {
        mvc.perform(get("/inventario-app/")).andExpect(status().isOk())
                .andExpect(forwardedUrl("/inventario-app/index.html"));
        mvc.perform(get("/inventario-app/index.html")).andExpect(status().isOk())
                .andExpect(content().string(org.hamcrest.Matchers.containsString("app-root")));
    }
    @Test void loginGeneralComparteSesionConInventarioYRespetaCargo() throws Exception {
        var usuario=usuarios.findByUsernameConRolesYPersona(almacenUser).orElseThrow();
        usuario.setPassword(passwordEncoder.encode("PruebaInventario1!"));em.flush();
        var respuesta=mvc.perform(post("/login").with(csrf()).param("username",almacenUser)
                .param("password","PruebaInventario1!"))
                .andExpect(status().is3xxRedirection()).andExpect(redirectedUrl("/admin/inventario")).andReturn();
        var sesion=(org.springframework.mock.web.MockHttpSession)respuesta.getRequest().getSession(false);
        mvc.perform(get("/api/v1/inventario/sesion").session(sesion)).andExpect(status().isOk())
                .andExpect(jsonPath("$.username").value(almacenUser));
    }
    @Test void loginRechazaCredencialesIncorrectas() throws Exception {
        mvc.perform(post("/api/v1/inventario/sesion/login").with(csrf()).contentType("application/json")
                .content("{\"username\":\""+almacenUser+"\",\"password\":\"incorrecta\"}"))
                .andExpect(status().isUnauthorized());
    }
    @Test void imagenDebeSerJpgOPngReal() throws Exception {
        var falso=new org.springframework.mock.web.MockMultipartFile("archivo","imagen.png","image/png","no es imagen".getBytes());
        mvc.perform(multipart("/api/v1/inventario/imagenes").file(falso).with(user(almacenUser).roles("EMPLEADO")).with(csrf()))
                .andExpect(status().isBadRequest());
        byte[] png=java.util.Base64.getDecoder().decode("iVBORw0KGgoAAAANSUhEUgAAAAEAAAABCAQAAAC1HAwCAAAAC0lEQVR42mP8/x8AAwMCAO+jRZkAAAAASUVORK5CYII=");
        var archivo=new org.springframework.mock.web.MockMultipartFile("archivo","imagen.png","image/png",png);
        var resultado=mvc.perform(multipart("/api/v1/inventario/imagenes").file(archivo).with(user(almacenUser).roles("EMPLEADO")).with(csrf()))
                .andExpect(status().isCreated()).andExpect(jsonPath("$.ruta").exists()).andReturn();
        String json=resultado.getResponse().getContentAsString();
        String ruta=json.substring(json.indexOf("/uploads/"),json.lastIndexOf('"'));
        java.nio.file.Path destino=java.nio.file.Path.of(ruta.substring(1)).toAbsolutePath();
        try { assertThat(java.nio.file.Files.exists(destino)).isTrue(); }
        finally { java.nio.file.Files.deleteIfExists(destino); }
    }
    @Test @Transactional(propagation=Propagation.NOT_SUPPORTED)
    void dosSalidasConcurrentesNoSobrevenden() throws Exception {
        ExecutorService pool=Executors.newFixedThreadPool(2);
        CountDownLatch inicio=new CountDownLatch(1);
        Callable<Boolean> salida=() -> {
            inicio.await();
            try { movimientos.registrar(salida(7),almacenUser); return true; }
            catch (InventarioException ex) { return false; }
        };
        try {
            Future<Boolean> a=pool.submit(salida),b=pool.submit(salida); inicio.countDown();
            assertThat(List.of(a.get(20,TimeUnit.SECONDS),b.get(20,TimeUnit.SECONDS)))
                    .containsExactlyInAnyOrder(true,false);
            assertThat(catalogo.obtener(TipoItem.PRODUCTO,productoId).stock()).isEqualTo(3);
            assertThat(movimientos.listar(TipoItem.PRODUCTO,productoId,TipoMovimiento.SALIDA,null,null,null)).hasSize(1);
        } finally { pool.shutdownNow(); }
    }
}
