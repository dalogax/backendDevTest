package com.capitole.similarproducts.application.service;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.lenient;

import com.capitole.similarproducts.domain.exception.ProductNotFoundException;
import com.capitole.similarproducts.domain.model.Product;
import com.capitole.similarproducts.domain.port.out.ProductServicePort;
import java.math.BigDecimal;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

@ExtendWith(MockitoExtension.class)
class SimilarProductsServiceTest {

    @Mock
    private ProductServicePort productServicePort;

    private com.capitole.similarproducts.configuration.SimilarProductsProperties properties;

    @InjectMocks
    private SimilarProductsService similarProductsService;

    @org.junit.jupiter.api.BeforeEach
    void setUp() {
         properties = new com.capitole.similarproducts.configuration.SimilarProductsProperties();
         properties.setMaxConcurrentCalls(10);
         similarProductsService = new SimilarProductsService(productServicePort, properties);
    }

    @Test
    void getSimilarProducts_ShouldReturnProducts_WhenFound() {
        String productId = "1";
        List<String> similarIds = List.of("2", "3");
        Product p2 = new Product("2", "Product 2", BigDecimal.TEN, true);
        Product p3 = new Product("3", "Product 3", BigDecimal.ONE, true);

        given(productServicePort.getSimilarProductIds(productId)).willReturn(Mono.just(similarIds));
        given(productServicePort.getProductDetail("2")).willReturn(Mono.just(p2));
        given(productServicePort.getProductDetail("3")).willReturn(Mono.just(p3));

        StepVerifier.create(similarProductsService.getSimilarProducts(productId))
                .expectNext(List.of(p2, p3))
                .verifyComplete();
    }

    @Test
    void getSimilarProducts_ShouldFilterOutNullOrInvalidProducts() {
        String productId = "1";
        List<String> similarIds = List.of("2", "3", "4");
        Product p2 = new Product("2", "Product 2", BigDecimal.TEN, true);
        // p4 causing error is handled by getProductDetailSafely returning empty

        given(productServicePort.getSimilarProductIds(productId)).willReturn(Mono.just(similarIds));
        given(productServicePort.getProductDetail("2")).willReturn(Mono.just(p2));
        given(productServicePort.getProductDetail("3")).willReturn(Mono.empty());
        given(productServicePort.getProductDetail("4")).willReturn(Mono.error(new RuntimeException("Fetch error")));

        StepVerifier.create(similarProductsService.getSimilarProducts(productId))
                .expectNext(List.of(p2))
                .verifyComplete();
    }

    @Test
    void getSimilarProducts_ShouldThrowProductNotFoundException_WhenIdsFetchFails() {
        String productId = "1";
        given(productServicePort.getSimilarProductIds(productId)).willReturn(Mono.error(new RuntimeException("API Error")));

        StepVerifier.create(similarProductsService.getSimilarProducts(productId))
                .expectError(ProductNotFoundException.class)
                .verify();
    }

    @Test
    void getSimilarProducts_ShouldPreserveOrder_WhenCallsProvideResultsOutOfOrder() {
        String productId = "1";
        List<String> similarIds = List.of("2", "3");
        Product p2 = new Product("2", "Product 2", BigDecimal.TEN, true);
        Product p3 = new Product("3", "Product 3", BigDecimal.ONE, true);

        given(productServicePort.getSimilarProductIds(productId)).willReturn(Mono.just(similarIds));
        
        // p2 is delayed, p3 is immediate. If flatMap was used, p3 might come first. 
        // With flatMapSequential, p2 should still be first.
        given(productServicePort.getProductDetail("2"))
            .willReturn(Mono.just(p2).delayElement(java.time.Duration.ofMillis(100)));
        given(productServicePort.getProductDetail("3"))
            .willReturn(Mono.just(p3));

        StepVerifier.create(similarProductsService.getSimilarProducts(productId))
                .expectNext(List.of(p2, p3))
                .verifyComplete();
    }
}
