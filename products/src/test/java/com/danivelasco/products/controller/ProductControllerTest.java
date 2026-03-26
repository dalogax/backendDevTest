package com.danivelasco.products.controller;

import com.danivelasco.products.dto.ProductDetailFailure;
import com.danivelasco.products.dto.ProductDetailsResponse;
import com.danivelasco.products.dto.ProductResponse;
import com.danivelasco.products.service.SimilarProductService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.http.HttpStatus;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

class ProductControllerTest {

    private SimilarProductService similarProductService;
    private ProductController productController;

    @BeforeEach
    void setUp() {
        similarProductService = Mockito.mock(SimilarProductService.class);
        productController = new ProductController(similarProductService);
    }

    @Test
    void getSimilarProducts_returnsOkResponseEntity() {
        ProductResponse product = new ProductResponse("2", "Shirt", 35.99, true);
        ProductDetailFailure failure = new ProductDetailFailure("3", 404);
        ProductDetailsResponse serviceResponse =
                new ProductDetailsResponse(List.of(product), List.of(failure));

        when(similarProductService.getSimilarProducts("1"))
                .thenReturn(Mono.just(serviceResponse));

        StepVerifier.create(productController.getSimilarProducts("1"))
                .assertNext(response -> {
                    assertEquals(HttpStatus.OK, response.getStatusCode());
                    assertEquals(serviceResponse, response.getBody());
                })
                .verifyComplete();
    }
}