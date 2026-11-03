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

@RequestMapping("/admin/inventario/movimientos")
public class AdminMovimientoInventarioController {
    private final MovimientoInventarioService movimientos;
    private final ProductoRepository productos;
    private final InsumoRepository insumos;
    @GetMapping
    public String listar(@RequestParam(value="comprobante",required=false) Long comprobante, Model model, Principal principal) {
        return "redirect:/inventario-app/index.html#/inventario/movimientos";
    }
    @PostMapping
    public String registrar(@ModelAttribute MovimientoInventarioForm form, Principal principal, RedirectAttributes redirect) {
        try {
            var resultado = movimientos.registrar(new MovimientoRequest(
                TipoMovimiento.valueOf(form.tipoMovimiento().trim().toUpperCase(java.util.Locale.ROOT)),
                TipoItem.valueOf(form.tipoItem().trim().toUpperCase(java.util.Locale.ROOT)),
                form.idItem(),form.cantidad(),"Movimiento manual"),principal.getName());
            redirect.addFlashAttribute("mensajeExito","Movimiento registrado correctamente.");
            return "redirect:/admin/inventario/movimientos?comprobante="+resultado.movimiento().idMovimiento();
        } catch (RuntimeException ex) {
            redirect.addFlashAttribute("mensajeError","No se pudo registrar el movimiento: revise el item, tipo, cantidad y stock disponible.");
            return "redirect:/admin/inventario/movimientos";
        }
    }
}
