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
        // Las credenciales de desarrollo se inyectan por variables o archivo local fuera del Git.
        assertThat(dev.getProperty("spring.datasource.url")).isEqualTo("${DB_URL}");
        assertThat(dev.getProperty("spring.datasource.username")).isEqualTo("${DB_USERNAME}");
        assertThat(dev.getProperty("spring.datasource.password")).isEqualTo("${DB_PASSWORD}");
        var prod = leer("application-prod.yaml");
        assertThat(prod.getProperty("spring.datasource.url")).isEqualTo("${DB_URL}");
        assertThat(prod.getProperty("spring.datasource.password")).isEqualTo("${DB_PASSWORD}");
        assertThat(prod.getProperty("springdoc.api-docs.enabled")).isEqualTo("false");
        assertThat(prod.getProperty("spring.config.import")).isNull();
    }
}
