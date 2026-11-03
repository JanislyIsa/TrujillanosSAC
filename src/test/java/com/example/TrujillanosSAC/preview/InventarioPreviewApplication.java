package com.example.TrujillanosSAC.preview;

import com.example.TrujillanosSAC.TrujillanosSacApplication;
import com.example.TrujillanosSAC.Model.*;
import com.example.TrujillanosSAC.inventario.dto.InventarioDtos.*;
import com.example.TrujillanosSAC.inventario.model.*;
import com.example.TrujillanosSAC.inventario.service.CatalogoInventarioService;
import jakarta.persistence.EntityManager;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.support.TransactionTemplate;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Stream;

/** Vista previa aislada: solo está en el classpath de pruebas; nunca en el JAR de producción. */
public class InventarioPreviewApplication {
    public static void main(String[] args) {
        String[] defaults={"--server.port=9192","--spring.datasource.url=jdbc:h2:mem:inventario-preview;MODE=Oracle;DB_CLOSE_DELAY=-1",
                "--spring.datasource.driver-class-name=org.h2.Driver","--spring.datasource.username=sa","--spring.datasource.password=",
                "--spring.jpa.database-platform=org.hibernate.dialect.H2Dialect","--spring.jpa.hibernate.ddl-auto=create-drop",
                "--app.seed.habilitado=false","--inventario.monitor.habilitado=false"};
        new SpringApplicationBuilder(TrujillanosSacApplication.class,DatosPreview.class)
                .run(Stream.concat(Arrays.stream(defaults),Arrays.stream(args)).toArray(String[]::new));
    }
    @TestConfiguration(proxyBeanMethods=false)
    static class DatosPreview {
        @Bean ApplicationRunner datosInventario(EntityManager em,CatalogoInventarioService catalogo,
                PasswordEncoder encoder,PlatformTransactionManager tx) {
            return args -> new TransactionTemplate(tx).executeWithoutResult(status -> sembrar(em,catalogo,encoder));
        }
        private void sembrar(EntityManager em,CatalogoInventarioService catalogo,PasswordEncoder encoder) {
            TipoDocumento doc=TipoDocumento.builder().nombre("Documento de prueba").abreviatura("TEST").longitud(8).build();em.persist(doc);
            Rol rol=Rol.builder().nombre("EMPLEADO").build();em.persist(rol);
            Empleado admin=empleado(em,encoder,doc,rol,"ADMIN","demo","David","Quezada");
            empleado(em,encoder,doc,rol,"JEFE_DE_COMPRAS","compras-demo","María","López");
            empleado(em,encoder,doc,rol,"ALMACENERO","almacen-demo","Carlos","Vargas");
            Almacen almacen=Almacen.builder().nombre("Almacén Principal").empleadoResponsable(admin).build();em.persist(almacen);
            Categoria categoria=Categoria.builder().nombre("Dulce").build();em.persist(categoria);
            PresentacionProducto presentacion=PresentacionProducto.builder().nombre("Paquete").build();em.persist(presentacion);
            UnidadMedida kg=UnidadMedida.builder().nombre("Kilogramo").build();em.persist(kg);
            UnidadMedida litro=UnidadMedida.builder().nombre("Litro").build();em.persist(litro);
            proveedor(em,doc,"Distribuidora Norte","norte@example.test");
            proveedor(em,doc,"Insumos La Libertad","insumos@example.test");
            em.persist(EstadoSolicitudCompra.builder().nombre("Pendiente").build());em.flush();
            String[] nombres={"Tetin POP","Chupa Rico","Trompitos","Acaramelados"};
            String[] imagenes={"tetin-pop.jpeg","chupa-rico.jpeg","trompipops.jpeg","acaramhelados.jpeg"};
            int[] stock={1240,150,3400,850},minimos={200,250,500,100},limites={5000,4000,8000,2000};
            for(int n=0;n<nombres.length;n++)catalogo.crearProducto(new ProductoRequest(nombres[n],
                    "Producto terminado de caramelo vendido por paquete.",BigDecimal.valueOf(12+n),stock[n],minimos[n],limites[n],
                    categoria.getIdCategoria(),presentacion.getIdPresentacion(),almacen.getIdAlmacen(),"/img/"+imagenes[n]),"demo");
            catalogo.crearInsumo(new InsumoRequest("Azúcar refinada",70,100,500,kg.getIdUnidadMedida(),almacen.getIdAlmacen()),"demo");
            catalogo.crearInsumo(new InsumoRequest("Glucosa",150,50,400,kg.getIdUnidadMedida(),almacen.getIdAlmacen()),"demo");
            catalogo.crearInsumo(new InsumoRequest("Esencia de fresa",8,10,40,litro.getIdUnidadMedida(),almacen.getIdAlmacen()),"demo");
        }
        private Persona persona(EntityManager em,TipoDocumento doc,String nombre,String apellido) {
            Persona p=Persona.builder().nombre(nombre).apellido(apellido).telefono("999999999").documento(doc)
                    .numDoc(UUID.randomUUID().toString().substring(0,8)).build();em.persist(p);return p;
        }
        private Empleado empleado(EntityManager em,PasswordEncoder encoder,TipoDocumento doc,Rol rol,String nombreCargo,
                String username,String nombre,String apellido) {
            Cargo cargo=Cargo.builder().nombre(nombreCargo).build();em.persist(cargo);
            Persona persona=persona(em,doc,nombre,apellido);
            Empleado e=Empleado.builder().persona(persona).salario(BigDecimal.valueOf(1500)).fechaContratacion(LocalDate.now()).cargo(cargo).build();em.persist(e);
            em.persist(Usuario.builder().username(username).password(encoder.encode("DemoInventario1!"))
                    .persona(persona).roles(new HashSet<>(Set.of(rol))).build());return e;
        }
        private void proveedor(EntityManager em,TipoDocumento doc,String nombre,String correo) {
            em.persist(Proveedor.builder().persona(persona(em,doc,nombre,"S.A.C.")).correo(correo).direccion("Trujillo").build());
        }
    }
}
