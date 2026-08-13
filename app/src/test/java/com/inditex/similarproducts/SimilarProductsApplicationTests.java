package com.inditex.similarproducts;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;

@SpringBootTest
@TestPropertySource(properties = "product-api.base-url=http://localhost:3001")
class SimilarProductsApplicationTests {

    @Test
    void contextLoads() {
    }
}
