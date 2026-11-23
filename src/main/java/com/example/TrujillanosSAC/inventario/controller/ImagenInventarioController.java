package com.example.TrujillanosSAC.inventario.controller;

import com.example.TrujillanosSAC.inventario.dto.response.ImagenResponse;
import com.example.TrujillanosSAC.inventario.service.service.ImagenInventarioService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/inventario/imagenes")
@PreAuthorize("hasRole('EMPLEADO')")
public class ImagenInventarioController {
    private final ImagenInventarioService imagenes;

    @PostMapping @ResponseStatus(HttpStatus.CREATED)
    public ImagenResponse subir(@RequestParam("archivo") MultipartFile archivo) {
        return imagenes.guardar(archivo);
    }
}
