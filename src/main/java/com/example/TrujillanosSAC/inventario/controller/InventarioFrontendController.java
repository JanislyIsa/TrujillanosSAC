package com.example.TrujillanosSAC.inventario.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class InventarioFrontendController {
    @GetMapping({"/inventario-app", "/inventario-app/"})
    public String frontend() {
        return "forward:/inventario-app/index.html";
    }
}
