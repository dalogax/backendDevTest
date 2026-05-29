package com.inditex.similarproducts.client;

import com.github.benmanes.caffeine.cache.Cache;
import com.inditex.similarproducts.exception.ExternalServiceException;
import com.inditex.similarproducts.exception.ProductNotFoundException;
import com.inditex.similarproducts.model.ProductDetail;
import io.github.resilience4j.circuitbreaker.CircuitBreaker;
import io.github.resilience4j.circuitbreaker.CircuitBreakerRegistry;
import io.github.resilience4j.reactor.circuitbreaker.operator.CircuitBreakerOperator;
import io.github.resilience4j.reactor.retry.RetryOperator;
import io.github.resilience4j.retry.Retry;
import io.github.resilience4j.retry.RetryRegistry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.List;

@Component
public class ProductApiClient {

    private static final Logger log = LoggerFactory.getLogger(ProductApiClient.class);

    private static final String CIRCUIT_BREAKER_NAME = "productClient";

    private final WebClient webClient;
    private final Cache<String, List<String>> similarIdsCache;
    private final Cache<String, ProductDetail> productDetailCache;
    private final CircuitBreaker circuitBreaker;
    private final Retry retry;

    public ProductApiClient(
            WebClient productWebClient,
            Cache<String, List<String>> similarIdsCache,
            Cache<String, ProductDetail> productDetailCache,
            CircuitBreakerRegistry circuitBreakerRegistry,
            RetryRegistry retryRegistry) {
        this.webClient = productWebClient;
        this.similarIdsCache = similarIdsCache;
        this.productDetailCache = productDetailCache;
        this.circuitBreaker = circuitBreakerRegistry.circuitBreaker(CIRCUIT_BREAKER_NAME);
        this.retry = retryRegistry.retry(CIRCUIT_BREAKER_NAME);
    }

    public Mono<List<String>> getSimilarIds(String productId) {
        List<String> cached = similarIdsCache.getIfPresent(productId);
        if (cached != null) {
            return Mono.just(cached);
        }

        return webClient.get()
                .uri("/product/{id}/similarids", productId)
                .retrieve()
                .onStatus(HttpStatusCode::is4xxClientError,
                        response -> Mono.error(new ProductNotFoundException(productId)))
                .onStatus(HttpStatusCode::is5xxServerError,
                        response -> Mono.error(new ExternalServiceException(
                                "Upstream error fetching similar IDs for product " + productId)))
                .bodyToMono(new ParameterizedTypeReference<List<String>>() {})
                .doOnNext(ids -> similarIdsCache.put(productId, ids))
                .transformDeferred(RetryOperator.of(retry))
                .transformDeferred(CircuitBreakerOperator.of(circuitBreaker));
    }

    public Mono<ProductDetail> getProductDetail(String productId) {
        ProductDetail cached = productDetailCache.getIfPresent(productId);
        if (cached != null) {
            return Mono.just(cached);
        }

        return webClient.get()
                .uri("/product/{id}", productId)
                .retrieve()
                .onStatus(HttpStatusCode::is4xxClientError,
                        response -> Mono.error(new ProductNotFoundException(productId)))
                .onStatus(HttpStatusCode::is5xxServerError,
                        response -> {
                            log.warn("Upstream 5xx for product {}", productId);
                            return Mono.error(new ExternalServiceException(
                                    "Upstream error fetching product " + productId));
                        })
                .bodyToMono(ProductDetail.class)
                .doOnNext(detail -> productDetailCache.put(productId, detail))
                .transformDeferred(CircuitBreakerOperator.of(circuitBreaker));
    }
}
