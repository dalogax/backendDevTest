package com.inditex.similarproducts.client;

import com.inditex.similarproducts.exception.ProductNotFoundException;
import com.inditex.similarproducts.model.ProductDetail;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.util.List;
import java.util.concurrent.TimeoutException;

@Component
public class ProductClient {

    private static final Logger log = LoggerFactory.getLogger(ProductClient.class);

    private final WebClient webClient;
    private final Duration detailTimeout;
    private final Duration similarIdsTimeout;

    public ProductClient(
            WebClient productWebClient,
            @Value("${product-api.detail-timeout-ms:2000}") long detailTimeoutMs,
            @Value("${product-api.similar-ids-timeout-ms:2000}") long similarIdsTimeoutMs) {
        this.webClient = productWebClient;
        this.detailTimeout = Duration.ofMillis(detailTimeoutMs);
        this.similarIdsTimeout = Duration.ofMillis(similarIdsTimeoutMs);
        log.info("Product API timeouts: similarIds={}ms, detail={}ms", similarIdsTimeoutMs, detailTimeoutMs);
    }

    /**
     * Entry-point call. Anything that goes wrong here fails the whole request, so failures are
     * logged at WARN — this is the signal that the upstream (or the connection pool) is degraded.
     */
    public Mono<List<String>> getSimilarIds(String productId) {
        return webClient.get()
                .uri("/product/{id}/similarids", productId)
                .exchangeToMono(response -> {
                    if (response.statusCode() == HttpStatus.NOT_FOUND) {
                        // An expected client outcome, not a fault: it becomes a 404 response.
                        log.debug("No similar IDs for product {}: upstream returned 404", productId);
                        return response.releaseBody()
                                .then(Mono.error(new ProductNotFoundException(productId)));
                    }
                    if (response.statusCode().isError()) {
                        log.warn("Upstream returned {} fetching similar IDs for product {}",
                                response.statusCode(), productId);
                        return response.releaseBody()
                                .then(Mono.error(new RuntimeException("Upstream error fetching similar IDs for product " + productId)));
                    }
                    return response.bodyToMono(new ParameterizedTypeReference<List<Object>>() {})
                            .map(ids -> ids.stream().map(Object::toString).toList());
                })
                // Bound the entry-point call so a stalled upstream can't hang the whole request.
                .timeout(similarIdsTimeout)
                .doOnError(TimeoutException.class, error ->
                        log.warn("Timed out after {}ms fetching similar IDs for product {}",
                                similarIdsTimeout.toMillis(), productId));
    }

    /**
     * Non-2xx responses and timeouts resolve to {@code Mono.empty()} — the product is skipped.
     *
     * <p>Skipping is designed behaviour, not a fault: products 1000 and 10000 time out on
     * essentially every request the load test makes. These are therefore logged at DEBUG — at any
     * higher level they would drown the log under load and hide the failures that actually matter.
     */
    public Mono<ProductDetail> getProductDetail(String productId) {
        return webClient.get()
                .uri("/product/{id}", productId)
                .exchangeToMono(response -> {
                    if (response.statusCode().is2xxSuccessful()) {
                        return response.bodyToMono(ProductDetail.class);
                    }
                    log.debug("Skipping product {}: upstream returned {}", productId, response.statusCode());
                    return response.releaseBody().then(Mono.empty());
                })
                .timeout(detailTimeout)
                .onErrorResume(error -> {
                    if (error instanceof TimeoutException) {
                        log.debug("Skipping product {}: no response within {}ms", productId, detailTimeout.toMillis());
                    } else {
                        log.debug("Skipping product {}: {}", productId, error.toString());
                    }
                    return Mono.empty();
                });
    }
}
