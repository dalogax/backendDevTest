package com.zara.similarproducts.application.usecase;

import com.zara.similarproducts.domain.model.Product;
import com.zara.similarproducts.domain.model.ProductId;
import com.zara.similarproducts.domain.model.ProductNotFoundException;
import com.zara.similarproducts.domain.model.SimilarProducts;
import com.zara.similarproducts.domain.service.SimilarProductsDomainService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.math.BigDecimal;
import java.time.Duration;
import java.util.List;
import java.util.concurrent.TimeoutException;

import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class GetSimilarProductsUseCaseImplTest {

    @Mock
    private SimilarProductsDomainService domainService;
    
    private GetSimilarProductsUseCaseImpl useCase;
    
    @BeforeEach
    void setUp() {
        useCase = new GetSimilarProductsUseCaseImpl(domainService);
    }

    @Test
    void shouldExecuteSuccessfully() {
        ProductId productId = ProductId.of("1");
        Product product = createProduct("2", "Similar Product");
        SimilarProducts similarProducts = SimilarProducts.of(List.of(product));
        
        when(domainService.findSimilarProducts(productId))
                .thenReturn(Mono.just(similarProducts));
        
        StepVerifier.create(useCase.execute(productId))
                .expectNext(similarProducts)
                .verifyComplete();
    }

    @Test
    void shouldPropagateProductNotFoundException() {
        ProductId productId = ProductId.of("1");
        
        when(domainService.findSimilarProducts(productId))
                .thenReturn(Mono.error(new ProductNotFoundException(productId)));
        
        StepVerifier.create(useCase.execute(productId))
                .expectError(ProductNotFoundException.class)
                .verify();
    }

    @Test
    void shouldApplyTimeout() {
        ProductId productId = ProductId.of("1");
        
        when(domainService.findSimilarProducts(productId))
                .thenReturn(Mono.never()); // Never completes
        
        StepVerifier.create(useCase.execute(productId))
                .expectError(TimeoutException.class)
                .verify(Duration.ofSeconds(15));
    }

    @Test
    void shouldPropagateGenericErrors() {
        ProductId productId = ProductId.of("1");
        RuntimeException error = new RuntimeException("Unexpected error");
        
        when(domainService.findSimilarProducts(productId))
                .thenReturn(Mono.error(error));
        
        StepVerifier.create(useCase.execute(productId))
                .expectError(RuntimeException.class)
                .verify();
    }

    private Product createProduct(String id, String name) {
        return Product.of(ProductId.of(id), name, BigDecimal.valueOf(10.0), true);
    }
}