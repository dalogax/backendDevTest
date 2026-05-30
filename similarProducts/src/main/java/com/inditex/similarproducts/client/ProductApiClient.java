package com.inditex.similarproducts.client;

import com.github.benmanes.caffeine.cache.AsyncLoadingCache;
import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.inditex.similarproducts.config.ProductApiProperties;
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

import java.time.Duration;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

@Component
public class ProductApiClient {

    private static final Logger log = LoggerFactory.getLogger(ProductApiClient.class);
    private static final String CIRCUIT_BREAKER_NAME = "productClient";

    // Virtual threads: lightweight, never competes with ForkJoinPool.commonPool() under load
    private static final java.util.concurrent.Executor CACHE_LOADER_EXECUTOR =
            Executors.newVirtualThreadPerTaskExecutor();

    private final WebClient webClient;
    private final Duration readTimeout;
    private final Cache<String, List<String>> similarIdsCache;
    private final AsyncLoadingCache<String, Optional<ProductDetail>> productDetailCache;
    private final CircuitBreaker circuitBreaker;
    private final Retry retry;

    public ProductApiClient(
            WebClient productWebClient,
            ProductApiProperties properties,
            Cache<String, List<String>> similarIdsCache,
            CircuitBreakerRegistry circuitBreakerRegistry,
            RetryRegistry retryRegistry) {
        this.webClient = productWebClient;
        this.readTimeout = Duration.ofMillis(properties.readTimeoutMs());
        this.similarIdsCache = similarIdsCache;
        this.circuitBreaker = circuitBreakerRegistry.circuitBreaker(CIRCUIT_BREAKER_NAME);
        this.retry = retryRegistry.retry(CIRCUIT_BREAKER_NAME);
        this.productDetailCache = buildProductDetailCache();
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
                // TimeoutException is not a WebClientRequestException → RetryOperator won't retry it
                .timeout(readTimeout)
                .transformDeferred(RetryOperator.of(retry))
                .transformDeferred(CircuitBreakerOperator.of(circuitBreaker));
    }

    public Mono<ProductDetail> getProductDetail(String productId) {
        // Fast path: already in cache and resolved — avoids CompletionStage overhead entirely
        CompletableFuture<Optional<ProductDetail>> present = productDetailCache.getIfPresent(productId);
        if (present != null && present.isDone() && !present.isCompletedExceptionally()) {
            Optional<ProductDetail> opt = present.join();
            return opt.map(Mono::just)
                    .orElseGet(() -> Mono.error(new ProductNotFoundException(productId)));
        }
        // Slow path: triggers a new load or joins a load already in flight for the same key
        return Mono.fromCompletionStage(productDetailCache.get(productId))
                .flatMap(opt -> opt.map(Mono::just)
                        .orElseGet(() -> Mono.error(new ProductNotFoundException(productId))));
    }

    // --- private helpers ---

    private AsyncLoadingCache<String, Optional<ProductDetail>> buildProductDetailCache() {
        return Caffeine.newBuilder()
                .maximumSize(5000)
                .expireAfterWrite(30, TimeUnit.SECONDS)
                // Dedicated executor so background loads never steal from ForkJoinPool.commonPool()
                .executor(CACHE_LOADER_EXECUTOR)
                .buildAsync((productId, executor) -> fetchProductDetailFromApi(productId).toFuture());
    }

    // 404 → Optional.empty() (cached — stops re-querying absent products)
    // 5xx → Mono.error       (not cached — Caffeine evicts the entry so next call retries)
    private Mono<Optional<ProductDetail>> fetchProductDetailFromApi(String productId) {
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
                .map(Optional::of)
                .onErrorResume(ProductNotFoundException.class, ex -> Mono.just(Optional.empty()))
                // Hard deadline: fires TimeoutException — not retried, recorded by circuit breaker
                .timeout(readTimeout)
                .transformDeferred(CircuitBreakerOperator.of(circuitBreaker));
    }
}
