package com.zara.similarproducts.infrastructure.adapter.in.rest.controller;

import com.zara.similarproducts.application.port.in.GetSimilarProductsUseCase;
import com.zara.similarproducts.domain.model.*;
import com.zara.similarproducts.infrastructure.adapter.in.rest.dto.ProductDetailResponse;
import com.zara.similarproducts.infrastructure.adapter.in.rest.mapper.SimilarProductsMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@WebFluxTest(SimilarProductsController.class)
class SimilarProductsControllerTest {

    @Autowired
    private WebTestClient webTestClient;
    
    @MockBean
    private GetSimilarProductsUseCase getSimilarProductsUseCase;
    
    @MockBean
    private SimilarProductsMapper mapper;

    @Test
    void shouldReturnSimilarProducts() {
        Product product = createProduct("2", "Similar Product");
        SimilarProducts similarProducts = SimilarProducts.of(List.of(product));
        ProductDetailResponse response = new ProductDetailResponse("2", "Similar Product", BigDecimal.valueOf(10.0), true);
        
        when(getSimilarProductsUseCase.execute(any(ProductId.class)))
                .thenReturn(Mono.just(similarProducts));
        when(mapper.toResponse(similarProducts))
                .thenReturn(List.of(response));
        
        webTestClient.get()
                .uri("/product/1/similar")
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .expectBodyList(ProductDetailResponse.class)
                .hasSize(1)
                .contains(response);
    }

    @Test
    void shouldReturnNotFoundWhenProductNotExists() {
        when(getSimilarProductsUseCase.execute(any(ProductId.class)))
                .thenReturn(Mono.error(new ProductNotFoundException(ProductId.of("1"))));
        
        webTestClient.get()
                .uri("/product/1/similar")
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isNotFound();
    }

    @Test
    void shouldReturnBadRequestForInvalidProductId() {
        when(getSimilarProductsUseCase.execute(any(ProductId.class)))
                .thenThrow(new InvalidProductIdException("abc"));
        
        webTestClient.get()
                .uri("/product/abc/similar")
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isBadRequest();
    }

    @Test
    void shouldReturnServiceUnavailableForExternalServiceError() {
        when(getSimilarProductsUseCase.execute(any(ProductId.class)))
                .thenReturn(Mono.error(new ExternalServiceException("Service unavailable")));
        
        webTestClient.get()
                .uri("/product/1/similar")
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().is5xxServerError();
    }

    private Product createProduct(String id, String name) {
        return Product.of(ProductId.of(id), name, BigDecimal.valueOf(10.0), true);
    }
}