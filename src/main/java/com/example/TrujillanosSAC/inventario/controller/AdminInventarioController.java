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

@Controller @RequiredArgsConstructor
@PreAuthorize("@inventarioPermisos.inventario(authentication)")

@RequestMapping("/admin/inventario")
public class AdminInventarioController {
    private final CatalogoInventarioService catalogo;
    private final ProductoRepository productos;
    private final InsumoRepository insumos;
    private final CategoriaRepository categorias;
    private final PresentacionProductoRepository presentaciones;
    private final UnidadMedidaRepository unidades;
    private final AlmacenRepository almacenes;
    @GetMapping
    public String listar(Model model, Principal principal) {
        return "redirect:/inventario-app/index.html#/inventario/catalogo";
    }
    @GetMapping("/control-stock")
    public String stock(Model model, Principal principal) {
        return "redirect:/inventario-app/index.html#/inventario/stock";
    }
    private void comunes(Model model, Principal principal) {
        model.addAttribute("usuarioNombre",principal.getName());
        model.addAttribute("productos",productos.findAllParaGestion());
        model.addAttribute("insumos",insumos.findAllParaInventario());
    }
    @PostMapping("/insumos")
    public String crear(@ModelAttribute InsumoAdminForm form, Principal principal, RedirectAttributes redirect) {
        try { catalogo.crearInsumo(request(form),principal.getName()); redirect.addFlashAttribute("mensajeExito","Insumo registrado correctamente."); }
        catch (RuntimeException ex) { redirect.addFlashAttribute("mensajeError","No se pudo registrar el insumo: revise sus datos y limites."); }
        return "redirect:/admin/inventario";
    }
    @PostMapping("/insumos/{id}/editar")
    public String editar(@PathVariable Long id, @ModelAttribute InsumoAdminForm form, RedirectAttributes redirect) {
        try { catalogo.editarInsumo(id,request(form)); redirect.addFlashAttribute("mensajeExito","Insumo actualizado correctamente."); }
        catch (RuntimeException ex) { redirect.addFlashAttribute("mensajeError","Revise los datos. Cambie las existencias mediante movimientos; conserve almacen y unidad."); }
        return "redirect:/admin/inventario";
    }
    @PostMapping("/insumos/{id}/eliminar")
    public String eliminar(@PathVariable Long id, RedirectAttributes redirect) {
        try { catalogo.eliminar(TipoItem.INSUMO,id); redirect.addFlashAttribute("mensajeExito","Insumo eliminado correctamente."); }
        catch (RuntimeException ex) { redirect.addFlashAttribute("mensajeError","El insumo tiene existencias o historial y debe conservarse."); }
        return "redirect:/admin/inventario";
    }
    private InsumoRequest request(InsumoAdminForm f) {
        Long almacen = f.idAlmacen() != null ? f.idAlmacen() : almacenes.findFirstByOrderByIdAlmacenAsc().orElseThrow().getIdAlmacen();
        return new InsumoRequest(f.nombre(),f.stock(),f.stockMinimo(),f.stockLimite(),f.idUnidadMedida(),almacen);
    }
}
