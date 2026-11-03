package com.example.TrujillanosSAC.inventario.service;
import com.example.TrujillanosSAC.inventario.dto.InventarioDtos.*;
import com.example.TrujillanosSAC.inventario.model.TipoItem;
import java.util.List;

public interface AlertaInventarioService {
    void sincronizar(TipoItem tipo, Long id);
    void monitorear();
    List<AlertaResponse> listar(boolean soloActivas);
    List<NotificacionResponse> notificaciones(String username);
    void marcarLeida(Long id, String username);
}
