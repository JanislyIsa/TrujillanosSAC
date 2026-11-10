package com.example.TrujillanosSAC;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.test.context.ActiveProfiles;
import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@ActiveProfiles("test")
class TrujillanosSacApplicationTests {
    @Autowired
    ApplicationContext context;

	@Test
	void contextLoads() {
		assertThat(context.getBean("detalleOrdenCompraRepository"))
				.isInstanceOf(com.example.TrujillanosSAC.Repository.DetalleOrdenCompraRepository.class);
		assertThat(context.getBean("comprasDetalleOrdenCompraRepository"))
				.isInstanceOf(com.example.TrujillanosSAC.compras.repository.DetalleOrdenCompraRepository.class);
	}

}
