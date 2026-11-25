package com.example.TrujillanosSAC.auditoria;

import com.example.TrujillanosSAC.auditoria.repository.AuditoriaEventoRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.support.TransactionTemplate;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ActiveProfiles("test")
@SpringBootTest
@AutoConfigureMockMvc
class AuditoriaIntegrationTests {
    @Autowired ApplicationEventPublisher eventos;
    @Autowired AuditoriaEventoRepository repository;
    @Autowired PlatformTransactionManager transactionManager;
    @Autowired MockMvc mvc;

    @Test
    void guardaEventoDespuesDelCommitYRestringeConsultaAAdministracion() throws Exception {
        String usuario = "auditoria-test";
        long antes = repository.count();
        new TransactionTemplate(transactionManager).executeWithoutResult(tx ->
                eventos.publishEvent(new EventoAuditoria(usuario, "STOCK_MOVIMIENTO_REGISTRADO",
                        "Movimiento", 987L, "Ingreso de prueba", "req-audit-01")));

        assertThat(repository.count()).isEqualTo(antes + 1);
        mvc.perform(get("/api/v1/auditoria").param("usuario", usuario)
                        .with(user("empleado").roles("EMPLEADO")))
                .andExpect(status().isForbidden());
        mvc.perform(get("/api/v1/auditoria").param("usuario", usuario)
                        .with(user("admin").roles("ADMIN")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.contenido[0].accion").value("STOCK_MOVIMIENTO_REGISTRADO"))
                .andExpect(jsonPath("$.contenido[0].requestId").value("req-audit-01"));
    }
}
