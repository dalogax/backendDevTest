package com.danivelasco.products.service;

import com.danivelasco.products.client.ProductApiClient;
import com.danivelasco.products.dto.ProductDetailFailure;
import com.danivelasco.products.dto.ProductDetailResult;
import com.danivelasco.products.dto.ProductDetailsResponse;
import com.danivelasco.products.dto.ProductResponse;
import com.danivelasco.products.exception.GlobalException;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.util.List;
import java.util.concurrent.TimeoutException;

/**
 * Service implementation responsible for retrieving similar products.
 * <p>
 * Combines multiple external API calls to build a complete response.
 * Includes caching, error handling and partial failure tolerance.
 */
@Service
public class SimilarProductServiceImpl implements SimilarProductService {

    private final ProductApiClient productApiClient;
    private final CacheManager cacheManager;

    public SimilarProductServiceImpl(ProductApiClient productApiClient, CacheManager cacheManager) {
        this.productApiClient = productApiClient;
        this.cacheManager = cacheManager;
    }

    /**
     * Retrieves similar products with a resilient and performant approach.
     *
     * <p>Key design decisions:
     * <ul>
     *     <li>Parallel fetching of product details using flatMap to reduce latency</li>
     *     <li>Controlled concurrency (flatMap with limit) to avoid overwhelming external APIs</li>
     *     <li>Partial failure handling: failed product calls do not break the whole response</li>
     *     <li>Timeout applied to each external call to prevent long blocking operations</li>
     *     <li>Caching applied to reduce repeated expensive calls</li>
     * </ul>
     *
     * <p>This design prioritizes availability and performance over strict consistency.
     */
    @Override
    public Mono<ProductDetailsResponse> getSimilarProducts(String productId) {
        Cache cache = cacheManager.getCache("similar_products_full");

        if (cache != null) {
            ProductDetailsResponse cachedValue = cache.get(productId, ProductDetailsResponse.class);

            if (cachedValue != null) {
                return Mono.just(cachedValue);
            }
        }

        return productApiClient.getSimilarProductIds(productId)
                .onErrorMap(WebClientResponseException.NotFound.class,
                        ex -> new GlobalException("No similar products found", HttpStatus.NOT_FOUND))
                .onErrorMap(WebClientResponseException.class,
                        ex -> new GlobalException("Upstream service error", HttpStatus.BAD_GATEWAY))
                .onErrorMap(TimeoutException.class,
                        ex -> new GlobalException("Upstream timeout", HttpStatus.GATEWAY_TIMEOUT))
                .flatMapMany(Flux::fromIterable)
                // Controlled concurrency:
                // We limit the number of parallel calls to 4 to avoid saturating the external service
                // and the WebClient connection pool. This value is intentionally conservative to
                // maintain system stability under high concurrency (200 VUs).
                //
                // A higher value could reduce latency for individual requests, but would significantly
                // increase the risk of connection pool exhaustion and degraded p95 latency.
                .flatMap(this::getProductDetailResult, 4)
                .collectList()
                .map(results -> {
                    List<ProductResponse> products = results.stream()
                            .filter(ProductDetailResult::isSuccess)
                            .map(ProductDetailResult::product)
                            .toList();

                    List<ProductDetailFailure> failures = results.stream()
                            .filter(result -> !result.isSuccess())
                            .map(ProductDetailResult::failure)
                            .toList();

                    return new ProductDetailsResponse(products, failures);
                })
                .doOnNext(result -> {
                    if (cache != null) {
                        cache.put(productId, result);
                    }
                });
    }

    /**
     * Retrieves product detail safely, converting errors into failure objects.
     *
     * @param productId product identifier
     * @return Mono with success or failure result
     */
    private Mono<ProductDetailResult> getProductDetailResult(String productId) {
        return productApiClient.getProductDetail(productId)
                // Timeout ensures that slow upstream responses do not block the entire flow.
                // This helps maintain responsiveness and avoids resource exhaustion.
                .timeout(Duration.ofSeconds(2))
                .map(ProductDetailResult::success)
                // Instead of failing the whole request, we capture individual failures
                // and return partial results. This improves system resilience and user experience.
                .onErrorResume(WebClientResponseException.class, ex ->
                        Mono.just(ProductDetailResult.failure(
                                new ProductDetailFailure(productId, ex.getStatusCode().value())
                        ))
                )
                .onErrorResume(ex ->
                        Mono.just(ProductDetailResult.failure(
                                new ProductDetailFailure(productId, 500)
                        ))
                );
    }
}
