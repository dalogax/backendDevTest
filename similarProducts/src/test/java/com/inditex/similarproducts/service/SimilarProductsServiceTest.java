package com.inditex.similarproducts.service;

import com.inditex.similarproducts.client.ProductApiClient;
import com.inditex.similarproducts.exception.ProductNotFoundException;
import com.inditex.similarproducts.model.ProductDetail;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.math.BigDecimal;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class SimilarProductsServiceTest {

    @Mock
    private ProductApiClient productApiClient;

    private SimilarProductsService service;

    private static final ProductDetail SHIRT = new ProductDetail("2", "Shirt", new BigDecimal("9.99"), true);
    private static final ProductDetail DRESS = new ProductDetail("3", "Dress", new BigDecimal("19.99"), true);
    private static final ProductDetail BLAZER = new ProductDetail("4", "Blazer", new BigDecimal("29.99"), false);

    @BeforeEach
    void setUp() {
        service = new SimilarProductsService(productApiClient);
    }

    @Test
    void returnsAllSimilarProducts_whenAllSucceed() {
        when(productApiClient.getSimilarIds("1")).thenReturn(Mono.just(List.of("2", "3", "4")));
        when(productApiClient.getProductDetail("2")).thenReturn(Mono.just(SHIRT));
        when(productApiClient.getProductDetail("3")).thenReturn(Mono.just(DRESS));
        when(productApiClient.getProductDetail("4")).thenReturn(Mono.just(BLAZER));

        StepVerifier.create(service.getSimilarProducts("1"))
                .assertNext(products -> {
                    assertThat(products).hasSize(3);
                    assertThat(products).containsExactly(SHIRT, DRESS, BLAZER);
                })
                .verifyComplete();
    }

    @Test
    void returnsEmptyList_whenNoSimilarIds() {
        when(productApiClient.getSimilarIds("1")).thenReturn(Mono.just(List.of()));

        StepVerifier.create(service.getSimilarProducts("1"))
                .assertNext(products -> assertThat(products).isEmpty())
                .verifyComplete();
    }

    @Test
    void propagatesNotFound_whenSimilarIdsEndpointReturns404() {
        when(productApiClient.getSimilarIds("99"))
                .thenReturn(Mono.error(new ProductNotFoundException("99")));

        StepVerifier.create(service.getSimilarProducts("99"))
                .expectError(ProductNotFoundException.class)
                .verify();
    }

    @Test
    void omitsFailedProduct_whenOneDetailCallFails() {
        when(productApiClient.getSimilarIds("1")).thenReturn(Mono.just(List.of("2", "3", "4")));
        when(productApiClient.getProductDetail("2")).thenReturn(Mono.just(SHIRT));
        when(productApiClient.getProductDetail("3")).thenReturn(Mono.error(new RuntimeException("timeout")));
        when(productApiClient.getProductDetail("4")).thenReturn(Mono.just(BLAZER));

        StepVerifier.create(service.getSimilarProducts("1"))
                .assertNext(products -> {
                    assertThat(products).hasSize(2);
                    assertThat(products).containsExactly(SHIRT, BLAZER);
                })
                .verifyComplete();
    }

    @Test
    void returnsEmptyList_whenAllDetailCallsFail() {
        when(productApiClient.getSimilarIds("1")).thenReturn(Mono.just(List.of("2", "3")));
        when(productApiClient.getProductDetail("2")).thenReturn(Mono.error(new RuntimeException("error")));
        when(productApiClient.getProductDetail("3")).thenReturn(Mono.error(new RuntimeException("error")));

        StepVerifier.create(service.getSimilarProducts("1"))
                .assertNext(products -> assertThat(products).isEmpty())
                .verifyComplete();
    }

    @Test
    void preservesSimilarityOrder_whenProductsReturnedOutOfOrder() {
        when(productApiClient.getSimilarIds("1")).thenReturn(Mono.just(List.of("2", "3", "4")));
        // Return them in reverse order via delayed Monos — flatMapSequential must preserve input order
        when(productApiClient.getProductDetail("2")).thenReturn(Mono.just(SHIRT).delayElement(java.time.Duration.ofMillis(30)));
        when(productApiClient.getProductDetail("3")).thenReturn(Mono.just(DRESS).delayElement(java.time.Duration.ofMillis(10)));
        when(productApiClient.getProductDetail("4")).thenReturn(Mono.just(BLAZER).delayElement(java.time.Duration.ofMillis(5)));

        StepVerifier.create(service.getSimilarProducts("1"))
                .assertNext(products -> assertThat(products).containsExactly(SHIRT, DRESS, BLAZER))
                .verifyComplete();
    }
}
