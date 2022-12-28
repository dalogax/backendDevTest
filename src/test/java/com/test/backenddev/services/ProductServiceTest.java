package com.test.backenddev.services;

import com.test.backenddev.client.SimilarProductsClient;
import com.test.backenddev.model.Product;
import feign.FeignException;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

@SpringBootTest
public class ProductServiceTest {

    @Mock
    private SimilarProductsClient similarProductsClient;

    @InjectMocks
    private ProductService productService;

    @Test
    public void shouldReturnNotFound_GetSimilarProductsWhenNotFoundProduct() {
        Product product = new Product("1", "Shirt", 9.99, true);
        List<String> products = List.of("1", "2");
        when(similarProductsClient.getSimilarProductIds("1")).thenReturn(products);
        when(similarProductsClient.getProductById("1")).thenReturn(product);
        when(similarProductsClient.getProductById("2")).thenThrow(FeignException.NotFound.class);

        try {
            productService.getSimilarProducts("1");
        } catch (Exception e) {
            assertEquals(FeignException.NotFound.class, e.getClass());
        }
    }

    @Test
    public void shouldGetSimilarProductsSatisfactorily() {
        Product product1 = new Product("1", "Shirt", 9.99, true);
        Product product2 = new Product("2", "Dress", 19.99, true);
        List<String> products = List.of("1", "2");
        when(similarProductsClient.getSimilarProductIds("1")).thenReturn(products);
        when(similarProductsClient.getProductById("1")).thenReturn(product1);
        when(similarProductsClient.getProductById("2")).thenReturn(product2);

        List<Product> actual = productService.getSimilarProducts("1");

        assertEquals(2, actual.size());
        assertTrue(actual.contains(product1));
        assertTrue(actual.contains(product2));
    }

    @Test
    public void shouldReturnException_GetSimilarProductsWhenInternalServerError() {
        Product product = new Product("1", "Jacket", 45.00, true);
        List<String> products = List.of("1", "2");
        when(similarProductsClient.getSimilarProductIds("1")).thenReturn(products);
        when(similarProductsClient.getProductById("1")).thenReturn(product);
        when(similarProductsClient.getProductById("2")).thenThrow(FeignException.InternalServerError.class);

        try {
            productService.getSimilarProducts("1");
        } catch (Exception e) {
            assertEquals(FeignException.InternalServerError.class, e.getClass());
        }
    }


}
