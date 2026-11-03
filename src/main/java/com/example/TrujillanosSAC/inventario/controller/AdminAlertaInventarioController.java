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

@RequestMapping("/admin/inventario/alertas")
public class AdminAlertaInventarioController {
    private final AlertaInventarioService alertas;
    private final CatalogoInventarioService catalogo;
    @GetMapping
    public String listar(Model model, Principal principal) {
        return "redirect:/inventario-app/index.html#/inventario/alertas";
    }
}
