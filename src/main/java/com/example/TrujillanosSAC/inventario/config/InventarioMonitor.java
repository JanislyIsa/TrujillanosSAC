package com.example.TrujillanosSAC.inventario.config;
import com.example.TrujillanosSAC.inventario.service.AlertaInventarioService;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@ConditionalOnProperty(name="inventario.monitor.habilitado", havingValue="true", matchIfMissing=true)
public class InventarioMonitor {
    private final AlertaInventarioService alertas;
    public InventarioMonitor(AlertaInventarioService alertas) { this.alertas = alertas; }
    @Scheduled(fixedDelayString="${inventario.monitor.intervalo-ms:60000}", initialDelayString="${inventario.monitor.inicio-ms:60000}")
    public void monitorear() { alertas.monitorear(); }
}
