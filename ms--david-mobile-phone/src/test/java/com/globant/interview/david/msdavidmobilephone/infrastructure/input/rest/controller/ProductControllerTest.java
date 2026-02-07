package com.globant.interview.david.msdavidmobilephone.infrastructure.input.rest.controller;

import com.globant.interview.david.msdavidmobilephone.application.usecase.GetSimilarProductsUseCase;
import com.globant.interview.david.msdavidmobilephone.domain.model.Product;
import com.globant.interview.david.msdavidmobilephone.domain.model.SimilarProducts;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProductControllerTest {

    @Mock
    private GetSimilarProductsUseCase getSimilarProductsUseCase;

    @InjectMocks
    private ProductController productController;

    @Test
    void shouldReturnSimilarProducts() {
        String productId = "1";
        Product p1 = new Product("2", "Samsung", new BigDecimal("899"), true);
        Product p2 = new Product("3", "iPhone", new BigDecimal("999"), true);
        SimilarProducts similarProducts = new SimilarProducts(List.of(p1, p2));

        when(getSimilarProductsUseCase.execute(productId)).thenReturn(similarProducts);

        ResponseEntity<List<Product>> response = productController.getSimilarProducts(productId);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(2, response.getBody().size());
        verify(getSimilarProductsUseCase, times(1)).execute(productId);
    }

    @Test
    void shouldReturnEmptyListWhenNoSimilarProducts() {
        String productId = "999";
        SimilarProducts similarProducts = SimilarProducts.empty();

        when(getSimilarProductsUseCase.execute(productId)).thenReturn(similarProducts);

        ResponseEntity<List<Product>> response = productController.getSimilarProducts(productId);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertTrue(response.getBody().isEmpty());
        verify(getSimilarProductsUseCase, times(1)).execute(productId);
    }

    @Test
    void shouldCallUseCaseWithCorrectProductId() {
        String productId = "123";
        SimilarProducts similarProducts = SimilarProducts.empty();

        when(getSimilarProductsUseCase.execute(productId)).thenReturn(similarProducts);

        productController.getSimilarProducts(productId);

        verify(getSimilarProductsUseCase, times(1)).execute("123");
    }
}
