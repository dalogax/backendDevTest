package com.sngular.similarproducts;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import com.sngular.similarproducts.application.outbound.ProductsPort;

@SpringBootTest
class SimilarproductsApplicationTests {

	@MockitoBean
	ProductsPort productsPort;

	@Test
	void contextLoads() {
	}

}
