package com.example.TrujillanosSAC.Controller;

import com.example.TrujillanosSAC.Service.AdminAccessService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.http.HttpStatus;

import java.security.Principal;

@Controller
@RequestMapping("/admin")
public class AdminController {

    private final AdminAccessService adminAccessService;

    public AdminController(AdminAccessService adminAccessService) {
        this.adminAccessService = adminAccessService;
    }

    @GetMapping({"/dashboard", "/{modulo}"})
    public String modulo(@PathVariable(value = "modulo", required = false) String modulo,
                         Model model,
                         Principal principal) {
        String nombreModulo = modulo == null ? "dashboard" : modulo;
        String url = "/admin/" + nombreModulo;

        if (!adminAccessService.puedeAccederA(principal.getName(), url)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN);
        }

        if ("compras".equals(nombreModulo)) {
            return "redirect:/admin/compras/solicitudes";
        }

        model.addAttribute("usuarioNombre", principal.getName());
        model.addAttribute("moduloActual", nombreModulo);
        return "admin/modulo";
    }
}
