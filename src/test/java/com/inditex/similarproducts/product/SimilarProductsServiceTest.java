package com.inditex.similarproducts.product;

import java.math.BigDecimal;
import java.net.URI;
import java.time.Duration;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.IntStream;

import com.inditex.similarproducts.catalog.CatalogClient;
import com.inditex.similarproducts.catalog.CatalogProperties;
import org.junit.jupiter.api.Test;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class SimilarProductsServiceTest {
    @Test
    void limitsConcurrentDetailRequestsWhilePreservingOrder() {
        CatalogClient catalog = mock(CatalogClient.class);
        CatalogProperties properties = new CatalogProperties(URI.create("http://localhost:3001"),
                Duration.ofSeconds(1), Duration.ofSeconds(6), Duration.ofSeconds(4), 2, 500, 1000);
        String[] ids = IntStream.range(0, 8).mapToObj(Integer::toString).toArray(String[]::new);
        AtomicInteger inFlight = new AtomicInteger();
        AtomicInteger peak = new AtomicInteger();
        when(catalog.similarIds("source")).thenReturn(Mono.just(ids));
        when(catalog.product(anyString())).thenAnswer(invocation -> {
            String id = invocation.getArgument(0);
            return Mono.defer(() -> {
                peak.accumulateAndGet(inFlight.incrementAndGet(), Math::max);
                return Mono.delay(Duration.ofMillis(100))
                        .map(ignored -> {
                            inFlight.decrementAndGet();
                            return new ProductDetail(id, "Item " + id, new BigDecimal("19.99"), true);
                        });
            });
        });

        StepVerifier.withVirtualTime(() -> new SimilarProductsService(catalog, properties).findSimilar("source"))
                .thenAwait(Duration.ofSeconds(1))
                .assertNext(products -> assertThat(products).extracting(ProductDetail::id).containsExactly(ids))
                .verifyComplete();

        assertThat(peak).hasValue(2);
        assertThat(inFlight).hasValue(0);
    }
}
