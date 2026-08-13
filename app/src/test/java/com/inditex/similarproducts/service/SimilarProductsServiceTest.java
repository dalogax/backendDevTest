package com.inditex.similarproducts.service;

import com.inditex.similarproducts.client.ProductClient;
import com.inditex.similarproducts.exception.ProductNotFoundException;
import com.inditex.similarproducts.model.ProductDetail;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.time.Duration;
import java.util.List;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

/**
 * Tests the aggregation contract: order follows similarity, details are fetched in
 * parallel, and unresolvable products drop out instead of failing the request.
 */
@ExtendWith(MockitoExtension.class)
class SimilarProductsServiceTest {

    private static final ProductDetail PRODUCT_2 = new ProductDetail("2", "Dress", 19.99, true);
    private static final ProductDetail PRODUCT_3 = new ProductDetail("3", "Blazer", 29.99, false);
    private static final ProductDetail PRODUCT_4 = new ProductDetail("4", "Boots", 39.99, true);

    @Mock
    private ProductClient productClient;

    @InjectMocks
    private SimilarProductsService service;

    @Test
    void returnsProductsInSimilarityOrder() {
        given(productClient.getSimilarIds("1")).willReturn(Mono.just(List.of("2", "3", "4")));
        given(productClient.getProductDetail("2")).willReturn(Mono.just(PRODUCT_2));
        given(productClient.getProductDetail("3")).willReturn(Mono.just(PRODUCT_3));
        given(productClient.getProductDetail("4")).willReturn(Mono.just(PRODUCT_4));

        StepVerifier.create(service.getSimilarProducts("1"))
                .expectNext(PRODUCT_2, PRODUCT_3, PRODUCT_4)
                .verifyComplete();
    }

    @Test
    void keepsSimilarityOrderEvenWhenAnEarlierProductRespondsLast() {
        // The upstream answers 4, then 3, then 2 — the response must still read 2, 3, 4.
        given(productClient.getSimilarIds("1")).willReturn(Mono.just(List.of("2", "3", "4")));
        given(productClient.getProductDetail("2"))
                .willAnswer(call -> Mono.just(PRODUCT_2).delayElement(Duration.ofMillis(300)));
        given(productClient.getProductDetail("3"))
                .willAnswer(call -> Mono.just(PRODUCT_3).delayElement(Duration.ofMillis(200)));
        given(productClient.getProductDetail("4"))
                .willAnswer(call -> Mono.just(PRODUCT_4).delayElement(Duration.ofMillis(100)));

        StepVerifier.create(service.getSimilarProducts("1"))
                .expectNext(PRODUCT_2, PRODUCT_3, PRODUCT_4)
                .verifyComplete();
    }

    @Test
    void fetchesDetailsInParallelRatherThanOneAfterAnother() {
        given(productClient.getSimilarIds("1")).willReturn(Mono.just(List.of("2", "3", "4")));
        // Stubs are built inside the answer so the delay binds to the virtual clock.
        given(productClient.getProductDetail("2"))
                .willAnswer(call -> Mono.just(PRODUCT_2).delayElement(Duration.ofSeconds(1)));
        given(productClient.getProductDetail("3"))
                .willAnswer(call -> Mono.just(PRODUCT_3).delayElement(Duration.ofSeconds(1)));
        given(productClient.getProductDetail("4"))
                .willAnswer(call -> Mono.just(PRODUCT_4).delayElement(Duration.ofSeconds(1)));

        // Three 1s calls complete after 1s in total. Sequential fetching would need 3s,
        // and this only advances the clock by 1s — so it fails if the calls stop overlapping.
        StepVerifier.withVirtualTime(() -> service.getSimilarProducts("1"))
                .expectSubscription()
                .thenAwait(Duration.ofSeconds(1))
                .expectNext(PRODUCT_2, PRODUCT_3, PRODUCT_4)
                .expectComplete()
                // Wall-clock bound: without it, sequential fetching leaves the verifier
                // waiting on virtual time that never advances, and the test hangs.
                .verify(Duration.ofSeconds(10));
    }

    @Test
    void dropsProductsThatCouldNotBeResolved() {
        // ProductClient turns 404s, 500s and timeouts into an empty Mono.
        given(productClient.getSimilarIds("4")).willReturn(Mono.just(List.of("2", "3", "4")));
        given(productClient.getProductDetail("2")).willReturn(Mono.just(PRODUCT_2));
        given(productClient.getProductDetail("3")).willReturn(Mono.empty());
        given(productClient.getProductDetail("4")).willReturn(Mono.just(PRODUCT_4));

        StepVerifier.create(service.getSimilarProducts("4"))
                .expectNext(PRODUCT_2, PRODUCT_4)
                .verifyComplete();
    }

    @Test
    void returnsNothingWhenEveryProductIsUnresolvable() {
        given(productClient.getSimilarIds("3")).willReturn(Mono.just(List.of("1000", "10000")));
        given(productClient.getProductDetail("1000")).willReturn(Mono.empty());
        given(productClient.getProductDetail("10000")).willReturn(Mono.empty());

        StepVerifier.create(service.getSimilarProducts("3"))
                .verifyComplete();
    }

    @Test
    void requestsNoDetailWhenThereAreNoSimilarIds() {
        given(productClient.getSimilarIds("1")).willReturn(Mono.just(List.of()));

        StepVerifier.create(service.getSimilarProducts("1"))
                .verifyComplete();

        verify(productClient, never()).getProductDetail(anyString());
    }

    @Test
    void propagatesNotFoundFromTheSimilarIdsCall() {
        given(productClient.getSimilarIds("999"))
                .willReturn(Mono.error(new ProductNotFoundException("999")));

        StepVerifier.create(service.getSimilarProducts("999"))
                .verifyError(ProductNotFoundException.class);
    }

    @Test
    void propagatesUpstreamFailureFromTheSimilarIdsCall() {
        given(productClient.getSimilarIds("1"))
                .willReturn(Mono.error(new RuntimeException("Upstream error fetching similar IDs for product 1")));

        StepVerifier.create(service.getSimilarProducts("1"))
                .verifyError(RuntimeException.class);
    }
}
