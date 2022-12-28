package com.test.backenddev.controller;

import com.test.backenddev.model.Product;
import com.test.backenddev.services.ProductService;
import feign.FeignException;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;
import static org.mockito.Mockito.when;

@SpringBootTest
public class SimilarProductsControllerTest {

    @Mock
    private ProductService productService;

    @InjectMocks
    private SimilarProductsController similarProductsController;

    @Test
    public void shouldReturn404_GetSimilarProductsWhenNotFound() {
        when(productService.getSimilarProducts("1")).thenThrow(FeignException.class);

        try {
            similarProductsController.getSimilarProducts("1");
            fail();
        } catch (Exception e) {
            assertEquals(FeignException.class, e.getClass());
        }
    }

    @Test
    public void shouldGetSimilarProductsSatisfactorily() {
        Product product1 = new Product("1", "Shirt", 9.99, true);
        Product product2 = new Product("2", "Dress", 19.99, true);
        List<Product> products = List.of(product1, product2);
        when(productService.getSimilarProducts("1")).thenReturn(products);

        ResponseEntity<List<Product>> actual = similarProductsController.getSimilarProducts("1");
        assertEquals(HttpStatus.OK, actual.getStatusCode());
        assertEquals(products, actual.getBody());
    }

    @Test
    public void shouldReturn500_GetSimilarProductsWhenThrowException() {
        when(productService.getSimilarProducts("1"))
                .thenThrow(FeignException.InternalServerError.class);

        try {
            similarProductsController.getSimilarProducts("1");
            fail();
        } catch (Exception e) {
            assertEquals(FeignException.InternalServerError.class, e.getClass());
        }
    }
}
