package com.zara.similarproducts.domain.service;

import com.zara.similarproducts.application.port.out.ProductRepository;
import com.zara.similarproducts.application.port.out.SimilarProductsRepository;
import com.zara.similarproducts.domain.model.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.math.BigDecimal;
import java.util.concurrent.TimeoutException;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class SimilarProductsDomainServiceTest {

    @Mock
    private SimilarProductsRepository similarProductsRepository;
    
    @Mock
    private ProductRepository productRepository;
    
    private SimilarProductsDomainService domainService;
    
    @BeforeEach
    void setUp() {
        domainService = new SimilarProductsDomainService(similarProductsRepository, productRepository);
    }

    @Test
    void shouldReturnSimilarProductsWhenFound() {
        ProductId productId = ProductId.of("1");
        ProductId similarId1 = ProductId.of("2");
        ProductId similarId2 = ProductId.of("3");
        
        Product product1 = createProduct(similarId1, "Product 2");
        Product product2 = createProduct(similarId2, "Product 3");
        
        when(similarProductsRepository.findSimilarProductIds(productId))
                .thenReturn(Flux.just(similarId1, similarId2));
        when(productRepository.findById(similarId1)).thenReturn(Mono.just(product1));
        when(productRepository.findById(similarId2)).thenReturn(Mono.just(product2));
        
        StepVerifier.create(domainService.findSimilarProducts(productId))
                .expectNextMatches(result -> 
                    result.size() == 2 && 
                    result.products().contains(product1) && 
                    result.products().contains(product2))
                .verifyComplete();
    }

    @Test
    void shouldThrowProductNotFoundWhenNoSimilarIds() {
        ProductId productId = ProductId.of("1");
        
        when(similarProductsRepository.findSimilarProductIds(productId))
                .thenReturn(Flux.empty());
        
        StepVerifier.create(domainService.findSimilarProducts(productId))
                .expectError(ProductNotFoundException.class)
                .verify();
    }

    @Test
    void shouldHandleTimeoutFromSimilarProductsRepository() {
        ProductId productId = ProductId.of("1");
        
        when(similarProductsRepository.findSimilarProductIds(productId))
                .thenReturn(Flux.error(new TimeoutException()));
        
        StepVerifier.create(domainService.findSimilarProducts(productId))
                .expectError(ExternalServiceException.class)
                .verify();
    }

    @Test
    void shouldHandleGenericErrorFromSimilarProductsRepository() {
        ProductId productId = ProductId.of("1");
        
        when(similarProductsRepository.findSimilarProductIds(productId))
                .thenReturn(Flux.error(new RuntimeException("Connection error")));
        
        StepVerifier.create(domainService.findSimilarProducts(productId))
                .expectError(ExternalServiceException.class)
                .verify();
    }

    @Test
    void shouldSkipProductsWithErrors() {
        ProductId productId = ProductId.of("1");
        ProductId similarId1 = ProductId.of("2");
        ProductId similarId2 = ProductId.of("3");
        
        Product product1 = createProduct(similarId1, "Product 2");
        
        when(similarProductsRepository.findSimilarProductIds(productId))
                .thenReturn(Flux.just(similarId1, similarId2));
        when(productRepository.findById(similarId1)).thenReturn(Mono.just(product1));
        when(productRepository.findById(similarId2)).thenReturn(Mono.error(new RuntimeException()));
        
        StepVerifier.create(domainService.findSimilarProducts(productId))
                .expectNextMatches(result -> 
                    result.size() == 1 && 
                    result.products().contains(product1))
                .verifyComplete();
    }

    @Test
    void shouldSkipProductsWithTimeout() {
        ProductId productId = ProductId.of("1");
        ProductId similarId1 = ProductId.of("2");
        ProductId similarId2 = ProductId.of("3");
        
        Product product1 = createProduct(similarId1, "Product 2");
        
        when(similarProductsRepository.findSimilarProductIds(productId))
                .thenReturn(Flux.just(similarId1, similarId2));
        when(productRepository.findById(similarId1)).thenReturn(Mono.just(product1));
        when(productRepository.findById(similarId2)).thenReturn(Mono.error(new TimeoutException()));
        
        StepVerifier.create(domainService.findSimilarProducts(productId))
                .expectNextMatches(result -> 
                    result.size() == 1 && 
                    result.products().contains(product1))
                .verifyComplete();
    }

    private Product createProduct(ProductId id, String name) {
        return Product.of(id, name, BigDecimal.valueOf(10.0), true);
    }
}