package com.example.TrujillanosSAC.inventario.controller;
import com.example.TrujillanosSAC.inventario.dto.InventarioDtos.*;
import com.example.TrujillanosSAC.inventario.model.*;
import com.example.TrujillanosSAC.inventario.repository.*;
import com.example.TrujillanosSAC.inventario.service.*;
import com.example.TrujillanosSAC.inventario.dto.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import java.security.Principal;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;
import java.math.BigDecimal;
import java.nio.file.*;
import java.util.Locale;
import java.util.UUID;

@Controller @RequiredArgsConstructor
@PreAuthorize("@inventarioPermisos.inventario(authentication)")

@RequestMapping("/admin/productos")
public class AdminProductoController {
    private final CatalogoInventarioService catalogo;
    private final ProductoRepository productos;
    private final CategoriaRepository categorias;
    private final PresentacionProductoRepository presentaciones;
    private final AlmacenRepository almacenes;
    @GetMapping
    public String listar(Model model, Principal principal) {
        return "redirect:/inventario-app/index.html#/inventario/catalogo";
    }
    @PostMapping
    public String crear(@ModelAttribute ProductoAdminForm form, @RequestParam(value="imagen",required=false) MultipartFile imagen,
            @RequestParam(value="returnTo",required=false) String returnTo, Principal principal, RedirectAttributes redirect) {
        try {
            Long almacen = almacenes.findFirstByOrderByIdAlmacenAsc().orElseThrow().getIdAlmacen();
            catalogo.crearProducto(request(form,almacen,guardarImagen(imagen)),principal.getName());
            redirect.addFlashAttribute("mensajeExito","Producto registrado correctamente.");
        } catch (IOException | RuntimeException ex) { redirect.addFlashAttribute("mensajeError","Revise los datos del producto y el formato de su imagen."); }
        return destino(returnTo);
    }
    @PostMapping("/{id}/editar")
    public String editar(@PathVariable Long id, @ModelAttribute ProductoAdminForm form,
            @RequestParam(value="imagen",required=false) MultipartFile imagen,
            @RequestParam(value="returnTo",required=false) String returnTo, RedirectAttributes redirect) {
        try {
            var item = catalogo.obtener(TipoItem.PRODUCTO,id);
            String ruta = guardarImagen(imagen);
            if (ruta == null) ruta = productos.findById(id).orElseThrow().getImagenRuta();
            catalogo.editarProducto(id,request(form,item.idAlmacen(),ruta));
            redirect.addFlashAttribute("mensajeExito","Producto actualizado correctamente.");
        } catch (IOException | RuntimeException ex) { redirect.addFlashAttribute("mensajeError","Revise los datos. Cambie el stock mediante movimientos y conserve la presentacion."); }
        return destino(returnTo);
    }
    @PostMapping("/{id}/eliminar")
    public String eliminar(@PathVariable Long id, @RequestParam(value="returnTo",required=false) String returnTo, RedirectAttributes redirect) {
        try { catalogo.eliminar(TipoItem.PRODUCTO,id); redirect.addFlashAttribute("mensajeExito","Producto eliminado correctamente."); }
        catch (RuntimeException ex) { redirect.addFlashAttribute("mensajeError","El producto tiene existencias o historial y debe conservarse."); }
        return destino(returnTo);
    }
    private String destino(String returnTo) { return "redirect:"+("/admin/inventario".equals(returnTo)?returnTo:"/admin/productos"); }
    private ProductoRequest request(ProductoAdminForm f, Long almacen, String imagen) {
        return new ProductoRequest(f.nombre(),f.descripcion()==null || f.descripcion().isBlank()?"Sin descripcion":f.descripcion(),
            f.precio(),f.stock(),f.stockMinimo(),f.stockLimite(),f.idCategoria(),f.idPresentacion(),almacen,imagen);
    }
    private String guardarImagen(MultipartFile imagen) throws IOException {
        if (imagen == null || imagen.isEmpty()) {
            return null;
        }

        String extension = obtenerExtension(imagen.getOriginalFilename());
        String nombreArchivo = "p-" + UUID.randomUUID().toString().replace("-", "").substring(0, 12) + extension;
        Path destino = Path.of("uploads", "productos").toAbsolutePath().normalize();
        Files.createDirectories(destino);
        Files.copy(imagen.getInputStream(), destino.resolve(nombreArchivo), StandardCopyOption.REPLACE_EXISTING);
        return "/uploads/productos/" + nombreArchivo;
    }

    private String obtenerExtension(String nombreArchivo) {
        if (nombreArchivo == null) {
            return ".jpg";
        }

        int punto = nombreArchivo.lastIndexOf('.');
        if (punto < 0 || punto == nombreArchivo.length() - 1) {
            return ".jpg";
        }

        String extension = nombreArchivo.substring(punto).toLowerCase(Locale.ROOT);
        if (!extension.matches("\\.(jpg|jpeg|png|webp)")) {
            throw new IllegalArgumentException("Formato de imagen no permitido");
        }
        return extension;
    }
}
