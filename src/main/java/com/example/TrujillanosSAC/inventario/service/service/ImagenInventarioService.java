package com.example.TrujillanosSAC.inventario.service.service;

import com.example.TrujillanosSAC.inventario.dto.response.ImagenResponse;
import org.springframework.web.multipart.MultipartFile;

public interface ImagenInventarioService {
    ImagenResponse guardar(MultipartFile archivo);
}
