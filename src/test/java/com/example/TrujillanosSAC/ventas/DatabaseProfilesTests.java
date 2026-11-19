package com.example.TrujillanosSAC.ventas;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.config.YamlPropertiesFactoryBean;
import org.springframework.core.io.ClassPathResource;
import java.util.Properties;
import static org.assertj.core.api.Assertions.assertThat;

class DatabaseProfilesTests {
    private Properties leer(String recurso) {
        var yaml = new YamlPropertiesFactoryBean();
        yaml.setResources(new ClassPathResource(recurso));
        return yaml.getObject();
    }

    @Test void perfilesConservanOracleSinModificarEsquema() {
        var base = leer("application.yaml");
        assertThat(base.getProperty("spring.profiles.default")).isEqualTo("dev");
        assertThat(base.getProperty("spring.jpa.hibernate.ddl-auto")).isEqualTo("validate");
        assertThat(base.getProperty("spring.datasource.driver-class-name")).isEqualTo("oracle.jdbc.OracleDriver");
        var dev = leer("application-dev.yaml");
        // En dev el equipo deja la conexión de Oracle escrita en el archivo; solo se comprueba que exista.
        assertThat(dev.getProperty("spring.datasource.url")).startsWith("jdbc:oracle:thin:@");
        assertThat(dev.getProperty("spring.datasource.username")).isNotBlank();
        var prod = leer("application-prod.yaml");
        assertThat(prod.getProperty("spring.datasource.url")).isEqualTo("${DB_URL}");
        assertThat(prod.getProperty("spring.datasource.password")).isEqualTo("${DB_PASSWORD}");
        assertThat(prod.getProperty("springdoc.api-docs.enabled")).isEqualTo("false");
        assertThat(prod.getProperty("spring.config.import")).isNull();
    }
}
