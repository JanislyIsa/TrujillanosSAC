package com.example.TrujillanosSAC.inventario.service.impl;

import com.example.TrujillanosSAC.inventario.dto.response.ImagenResponse;
import com.example.TrujillanosSAC.inventario.exception.InventarioException;
import com.example.TrujillanosSAC.inventario.service.service.ImagenInventarioService;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Locale;
import java.util.Set;
import java.util.UUID;

@Service
public class ImagenInventarioServiceImpl implements ImagenInventarioService {
    private static final long TAMANIO_MAXIMO = 2 * 1024 * 1024;
    private static final int DIMENSION_MAXIMA = 8000;

    @Override
    public ImagenResponse guardar(MultipartFile archivo) {
        if (archivo == null || archivo.isEmpty() || archivo.getSize() > TAMANIO_MAXIMO)
            throw new IllegalArgumentException("Selecciona una imagen JPG o PNG de hasta 2 MB");

        String formato;
        try (var entrada = ImageIO.createImageInputStream(archivo.getInputStream())) {
            var lectores = ImageIO.getImageReaders(entrada);
            if (!lectores.hasNext()) throw new IllegalArgumentException("Imagen invalida");
            var lector = lectores.next();
            try {
                lector.setInput(entrada);
                formato = lector.getFormatName().toLowerCase(Locale.ROOT);
                if (!Set.of("png", "jpeg", "jpg").contains(formato)
                        || lector.getWidth(0) > DIMENSION_MAXIMA || lector.getHeight(0) > DIMENSION_MAXIMA)
                    throw new IllegalArgumentException("Utiliza una imagen JPG o PNG de hasta 8000 píxeles por lado");
            } finally {
                lector.dispose();
            }

            String nombre = UUID.randomUUID() + (formato.equals("png") ? ".png" : ".jpg");
            Path directorio = Path.of("uploads", "productos").toAbsolutePath().normalize();
            Files.createDirectories(directorio);
            try (var contenido = archivo.getInputStream()) {
                Files.copy(contenido, directorio.resolve(nombre));
            }
            return new ImagenResponse("/uploads/productos/" + nombre);
        } catch (IOException ex) {
            throw InventarioException.conflicto("No se pudo guardar la imagen; vuelve a intentarlo");
        }
    }
}
