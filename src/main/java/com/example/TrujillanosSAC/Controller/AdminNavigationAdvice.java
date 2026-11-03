package com.example.TrujillanosSAC.Controller;

import com.example.TrujillanosSAC.Service.AdminAccessService;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

import java.security.Principal;

@ControllerAdvice
public class AdminNavigationAdvice {

    private final AdminAccessService adminAccessService;

    public AdminNavigationAdvice(AdminAccessService adminAccessService) {
        this.adminAccessService = adminAccessService;
    }

    @ModelAttribute
    public void agregarPanelAdmin(Model model, Principal principal) {
        boolean puedeAbrirPanel = principal != null && adminAccessService.puedeAbrirPanel(principal.getName());

        model.addAttribute("puedeAbrirPanelAdmin", puedeAbrirPanel);
        model.addAttribute("cargoAdmin", puedeAbrirPanel ? adminAccessService.obtenerCargo(principal.getName()) : "");
        model.addAttribute("modulosAdmin", puedeAbrirPanel ? adminAccessService.obtenerModulos(principal.getName()) : java.util.List.of());
    }
}
