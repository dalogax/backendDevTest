package com.prueba.products.controller;

import com.prueba.products.model.Product;
import com.prueba.products.service.SimilarProductsService;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.List;

import static org.mockito.Mockito.when;


@SpringBootTest
public class SimilarProductsControllerTest {

    @InjectMocks
    private SimilarProductsController similarProductsController;

    @Mock
    private SimilarProductsService similarProductsService;

    @Test
    public void shouldGetResponse200WhenGetSimilarProducts() {

        Product product1 = new Product("2", "Dress", 19.99, true);
        Product product2 = new Product("3", "Blazer", 29.99, false);
        Product product3 = new Product("4", "Boots", 39.99, true);
        List<Product> products = List.of(product1, product2, product3);

        when(similarProductsService.getSimilarProducts("1")).thenReturn(products);
        ResponseEntity result = (ResponseEntity) similarProductsController.getSimilarProducts("1");
        assertEquals(result ,ResponseEntity.ok(products));
    }

    @Test
    public void shouldGetResponse404WhenGetSimilarProductsNotFountProducts() {

        ResponseEntity error = ResponseEntity.status(HttpStatus.NOT_FOUND).body("Product not Found");

        when(similarProductsService.getSimilarProducts("1")).thenReturn(null);
        ResponseEntity result = (ResponseEntity) similarProductsController.getSimilarProducts("1");
        assertEquals(result ,error);
    }
}
