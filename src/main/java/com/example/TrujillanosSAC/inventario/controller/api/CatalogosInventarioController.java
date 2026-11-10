package com.example.TrujillanosSAC.inventario.controller.api;
import com.example.TrujillanosSAC.inventario.dto.InventarioDtos.CatalogoResponse;
import com.example.TrujillanosSAC.inventario.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.web.csrf.CsrfToken;
import org.springframework.web.bind.annotation.*;
import java.util.*;

@RestController @RequiredArgsConstructor
@RequestMapping("/api/v1/inventario")
public class CatalogosInventarioController {
    private final CategoriaRepository categorias;
    private final PresentacionProductoRepository presentaciones;
    private final UnidadMedidaRepository unidades;
    private final AlmacenRepository almacenes;
    private final com.example.TrujillanosSAC.usuarios.repository.ProveedorRepository proveedores;
    @GetMapping("/proveedores") @PreAuthorize("@inventarioPermisos.compras(authentication)")
    @org.springframework.transaction.annotation.Transactional(readOnly=true)
    public java.util.List<CatalogoResponse> proveedores() {
        return proveedores.findAll().stream().map(p -> new CatalogoResponse(p.getIdPersona(),
                p.getPersona().getNombre()+" "+p.getPersona().getApellido())).toList();
    }
    @GetMapping("/csrf")
    public Map<String,String> csrf(CsrfToken csrf) { return Map.of("headerName",csrf.getHeaderName(),"token",csrf.getToken()); }
    @GetMapping("/catalogos") @PreAuthorize("@inventarioPermisos.lectura(authentication)")
    public Map<String,List<CatalogoResponse>> catalogos() {
        return Map.of("categorias",categorias.findAll().stream().map(c -> new CatalogoResponse(c.getIdCategoria(),c.getNombre())).toList(),
                "presentaciones",presentaciones.findAll().stream().map(p -> new CatalogoResponse(p.getIdPresentacion(),p.getNombre())).toList(),
                "unidadesMedida",unidades.findAll().stream().map(u -> new CatalogoResponse(u.getIdUnidadMedida(),u.getNombre())).toList(),
                "almacenes",almacenes.findAll().stream().map(a -> new CatalogoResponse(a.getIdAlmacen(),a.getNombre())).toList());
    }
}
