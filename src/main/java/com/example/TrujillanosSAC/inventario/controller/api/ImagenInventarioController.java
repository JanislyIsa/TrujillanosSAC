package com.example.TrujillanosSAC.inventario.controller.api;

import com.example.TrujillanosSAC.inventario.exception.InventarioException;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import javax.imageio.ImageIO;
import java.io.IOException;
import java.nio.file.*;
import java.util.*;

@RestController
@RequestMapping("/api/v1/inventario/imagenes")
@PreAuthorize("@inventarioPermisos.inventario(authentication)")
public class ImagenInventarioController {
    public record ImagenResponse(String ruta) {}
    @PostMapping @ResponseStatus(HttpStatus.CREATED)
    public ImagenResponse subir(@RequestParam("archivo") MultipartFile archivo) throws IOException {
        if (archivo.isEmpty() || archivo.getSize()>2*1024*1024)
            throw new IllegalArgumentException("Selecciona una imagen JPG o PNG de hasta 2 MB");
        String formato;
        try (var entrada=ImageIO.createImageInputStream(archivo.getInputStream())) {
            var lectores=ImageIO.getImageReaders(entrada);
            if (!lectores.hasNext()) throw new IllegalArgumentException("Imagen invalida");
            var lector=lectores.next();
            try {
                lector.setInput(entrada);
                formato=lector.getFormatName().toLowerCase(Locale.ROOT);
                if (!Set.of("png","jpeg","jpg").contains(formato) || lector.getWidth(0)>8000 || lector.getHeight(0)>8000)
                    throw new IllegalArgumentException("Utiliza una imagen JPG o PNG de hasta 8000 píxeles por lado");
            } finally { lector.dispose(); }
        }
        String nombre=UUID.randomUUID()+(formato.equals("png")?".png":".jpg");
        Path directorio=Path.of("uploads","productos").toAbsolutePath().normalize();
        try {
            Files.createDirectories(directorio);
            try (var entrada=archivo.getInputStream()) { Files.copy(entrada,directorio.resolve(nombre)); }
        } catch (IOException ex) { throw InventarioException.conflicto("No se pudo guardar la imagen; vuelve a intentarlo"); }
        return new ImagenResponse("/uploads/productos/"+nombre);
    }
}
