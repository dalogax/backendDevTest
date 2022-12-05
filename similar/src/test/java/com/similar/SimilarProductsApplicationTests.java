package com.similar;

import com.similar.application.SimilarProductController;
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

	@Test
	void contextLoads() {
		assertNotNull(applicationContext);
		assertNotNull(similarProductController);
	}

}
