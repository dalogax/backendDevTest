package com.similar;

import com.similar.application.SimilarProductController;
import com.similar.domain.SimilarProductService;
import com.similar.domain.impl.SimilarProductServiceImpl;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;

import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest
class SimilarProductsApplicationTests {

	@Autowired
	ApplicationContext applicationContext;

	@Autowired
	SimilarProductController similarProductController;

	@Autowired
	SimilarProductServiceImpl similarProductService;

	@Test
	void contextLoads() {
		assertNotNull(applicationContext);
		assertNotNull(similarProductController);
		assertNotNull(similarProductService);
	}

}
