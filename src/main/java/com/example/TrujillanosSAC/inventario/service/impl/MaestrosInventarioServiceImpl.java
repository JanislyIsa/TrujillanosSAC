package com.example.TrujillanosSAC.inventario.service.impl;

import com.example.TrujillanosSAC.inventario.dto.response.CatalogoResponse;
import com.example.TrujillanosSAC.inventario.repository.AlmacenRepository;
import com.example.TrujillanosSAC.inventario.repository.CategoriaRepository;
import com.example.TrujillanosSAC.inventario.repository.PresentacionProductoRepository;
import com.example.TrujillanosSAC.inventario.repository.UnidadMedidaRepository;
import com.example.TrujillanosSAC.inventario.service.service.MaestrosInventarioService;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MaestrosInventarioServiceImpl implements MaestrosInventarioService {
    private final CategoriaRepository categorias;
    private final PresentacionProductoRepository presentaciones;
    private final UnidadMedidaRepository unidades;
    private final AlmacenRepository almacenes;

    @Override
    @Cacheable(cacheNames = "inventario-catalogos", key = "'maestros-v1'")
    public Map<String, List<CatalogoResponse>> catalogos() {
        return Map.of(
                "categorias", categorias.findAll().stream()
                        .map(c -> new CatalogoResponse(c.getIdCategoria(), c.getNombre())).toList(),
                "presentaciones", presentaciones.findAll().stream()
                        .map(p -> new CatalogoResponse(p.getIdPresentacion(), p.getNombre())).toList(),
                "unidadesMedida", unidades.findAll().stream()
                        .map(u -> new CatalogoResponse(u.getIdUnidadMedida(), u.getNombre())).toList(),
                "almacenes", almacenes.findAll().stream()
                        .map(a -> new CatalogoResponse(a.getIdAlmacen(), a.getNombre())).toList());
    }
}
