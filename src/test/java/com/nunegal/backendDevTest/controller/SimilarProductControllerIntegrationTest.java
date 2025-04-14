package com.nunegal.backendDevTest.controller;

import com.nunegal.backendDevTest.client.ProductClient;
import com.nunegal.backendDevTest.model.Product;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.ResponseEntity;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

@SpringBootTest
public class SimilarProductControllerIntegrationTest {

    @Autowired
    private SimilarProductController controller;

    @MockBean
    private ProductClient productClient;

    @Test
    public void testGetSimilarProducts_IntegrationFlow() {
        // Arrange
        String productId = "1";
        List<Integer> ids = Arrays.asList(2, 3);

        Product product1 = new Product("2", "Shirt", 9.99, true);
        Product product2 = new Product("3", "Shoes", 19.99, true);

        when(productClient.getSimilarProductIds(productId)).thenReturn(ids);
        when(productClient.getProductById("2")).thenReturn(product1);
        when(productClient.getProductById("3")).thenReturn(product2);

        // Act
        ResponseEntity<List<Product>> response = controller.getSimilarProducts(productId);

        // Assert
        assertEquals(200, response.getStatusCodeValue());
        assertEquals(2, response.getBody().size());
        assertEquals("2", response.getBody().get(0).getId());
        assertEquals("3", response.getBody().get(1).getId());
    }

}
