package com.example.demo.controller;

import com.example.demo.model.Product;
import com.example.demo.service.ProductService;
import feign.FeignException;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@SpringBootTest
class ProductControllerTest {

    @Mock
    private ProductService productService;
    @InjectMocks
    private ProductController underTest;

    @Test
    public void shouldGetSimilarProductsOk() {
        Product p1 = new Product("1", "Shirt", 9.99, true);
        Product p2 = new Product("2", "Dress", 19.99, true);
        Set<Product> products = Set.of(p1, p2);
        when(productService.getSimilarProducts("1")).thenReturn(products);

        ResponseEntity<Set<Product>> actual = underTest.getSimilarProducts("1");

        assertEquals(HttpStatus.OK, actual.getStatusCode());
        assertEquals(products, actual.getBody());
    }

    @Test
    public void shouldReturn404_GetSimilarProductsWhenNotFound() {
        when(productService.getSimilarProducts("1")).thenThrow(FeignException.class);

        try {
            underTest.getSimilarProducts("1");
            fail();
        } catch (Exception e) {
            assertEquals(FeignException.class, e.getClass());
        }
    }

    @Test
    public void shouldReturn500_GetSimilarProductsWhenThrowException() {
        when(productService.getSimilarProducts("1"))
                .thenThrow(FeignException.InternalServerError.class);

        try {
            underTest.getSimilarProducts("1");
            fail();
        } catch (Exception e) {
            assertEquals(FeignException.InternalServerError.class, e.getClass());
        }
    }
}