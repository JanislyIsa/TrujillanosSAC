package com.example.TrujillanosSAC.inventario.controller;
import com.example.TrujillanosSAC.inventario.dto.InsumoAdminForm;
import com.example.TrujillanosSAC.inventario.dto.request.InsumoRequest;
import com.example.TrujillanosSAC.inventario.model.TipoItem;
import com.example.TrujillanosSAC.inventario.repository.AlmacenRepository;
import com.example.TrujillanosSAC.inventario.service.service.CatalogoInventarioService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import java.security.Principal;

@Controller
@PreAuthorize("@inventarioPermisos.inventario(authentication)")
@RequestMapping("/admin")
public class AdminInventarioController {
    private final CatalogoInventarioService catalogo;
    private final AlmacenRepository almacenes;
    private final String frontendEmpleadosUrl;

    public AdminInventarioController(CatalogoInventarioService catalogo, AlmacenRepository almacenes,
                                     @Value("${app.frontend.empleados-url}") String frontendEmpleadosUrl) {
        this.catalogo = catalogo;
        this.almacenes = almacenes;
        this.frontendEmpleadosUrl = frontendEmpleadosUrl.replaceFirst("/+$", "");
    }

    @GetMapping({"/inventario", "/productos"})
    public String catalogo() {
        return irAlPanel("catalogo");
    }

    @GetMapping("/inventario/control-stock")
    public String stock() {
        return irAlPanel("stock");
    }

    @GetMapping("/inventario/movimientos")
    public String movimientos() {
        return irAlPanel("movimientos");
    }

    @GetMapping("/inventario/alertas")
    public String alertas() {
        return irAlPanel("alertas");
    }

    private String irAlPanel(String pagina) {
        return "redirect:" + frontendEmpleadosUrl + "/#/inventario/" + pagina;
    }

    @PostMapping("/inventario/insumos")
    public String crear(@ModelAttribute InsumoAdminForm form, Principal principal, RedirectAttributes redirect) {
        try { catalogo.crearInsumo(request(form),principal.getName()); redirect.addFlashAttribute("mensajeExito","Insumo registrado correctamente."); }
        catch (RuntimeException ex) { redirect.addFlashAttribute("mensajeError","No se pudo registrar el insumo: revise sus datos y limites."); }
        return "redirect:/admin/inventario";
    }
    @PostMapping("/inventario/insumos/{id}/editar")
    public String editar(@PathVariable Long id, @ModelAttribute InsumoAdminForm form, RedirectAttributes redirect) {
        try { catalogo.editarInsumo(id,request(form)); redirect.addFlashAttribute("mensajeExito","Insumo actualizado correctamente."); }
        catch (RuntimeException ex) { redirect.addFlashAttribute("mensajeError","Revise los datos. Cambie las existencias mediante movimientos; conserve almacen y unidad."); }
        return "redirect:/admin/inventario";
    }
    @PostMapping("/inventario/insumos/{id}/eliminar")
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
