package com.capitole.similarproducts.adapter.out.client;

import com.capitole.similarproducts.generated.api.ProductApi;

import com.capitole.similarproducts.adapter.out.client.mapper.ProductClientMapper;
import com.capitole.similarproducts.domain.exception.ExternalServiceException;
import com.capitole.similarproducts.domain.model.Product;
import com.capitole.similarproducts.domain.port.out.ProductServicePort;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import java.util.List;
import org.jspecify.annotations.NonNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Mono;

/**
 * Provides reactive non-blocking HTTP calls to external product service.
 * Uses OpenAPI-generated models for type safety.
 */
@Component
public class ProductApiClient implements ProductServicePort {

    private static final Logger LOGGER = LoggerFactory.getLogger(ProductApiClient.class);

    private static final String SERVICE_NAME = "product-service";
    private static final String CIRCUIT_BREAKER_NAME = "productService";
    private static final String RETRY_NAME = "productService";

    private final ProductApi productApi;
    private final ProductClientMapper productClientMapper;

    public ProductApiClient(ProductApi productApi, ProductClientMapper productClientMapper) {
        this.productApi = productApi;
        this.productClientMapper = productClientMapper;
        LOGGER.info("ProductApiClient initialized with OpenAPI-generated models");
    }

    /**
     * Retrieves similar product IDs from external service.
     * Applies circuit breaker and retry patterns for resilience.
     *
     * @param productId the product ID
     * @return Mono containing list of similar product IDs
     */
    @Override
    @Cacheable(value = "similarProductIds", key = "#productId")
    @CircuitBreaker(name = CIRCUIT_BREAKER_NAME, fallbackMethod = "getSimilarProductIdsFallback")
    @Retry(name = RETRY_NAME, fallbackMethod = "getSimilarProductIdsFallback")
    public Mono<@NonNull List<String>> getSimilarProductIds(String productId) {
        LOGGER.debug("Fetching similar product IDs for productId: {}", productId);

        return productApi.getProductSimilarids(productId)
                .map(ids -> (List<String>) new java.util.ArrayList<>(ids))
                .doOnSuccess(ids -> LOGGER.debug("Retrieved {} similar product IDs for productId: {}",
                    ids.size(), productId))
                .doOnError(WebClientResponseException.NotFound.class,
                        error -> LOGGER.warn("Product not found: {}", productId))
                .doOnError(error -> LOGGER.error("Error fetching similar product IDs for productId: {}",
                        productId, error));
    }

    /**
     * Retrieves product details from external service.
     * Applies circuit breaker and retry patterns for resilience.
     *
     * @param productId the product ID
     * @return Mono containing product details, or empty if not found
     */
    @Override
    @Cacheable(value = "products", key = "#productId")
    @CircuitBreaker(name = CIRCUIT_BREAKER_NAME, fallbackMethod = "getProductDetailFallback")
    @Retry(name = RETRY_NAME, fallbackMethod = "getProductDetailFallback")
    public Mono<Product> getProductDetail(String productId) {
        LOGGER.debug("Fetching product detail for productId: {}", productId);

        return productApi.getProductProductId(productId)
                .map(productClientMapper::toDomain)
                .doOnSuccess(product -> LOGGER.debug("Retrieved product detail for productId: {}", productId))
                .doOnError(WebClientResponseException.NotFound.class,
                        error -> LOGGER.debug("Product not found: {}", productId))
                .onErrorResume(WebClientResponseException.NotFound.class, error -> Mono.empty())
                .doOnError(error -> LOGGER.error("Error fetching product detail for productId: {}",
                        productId, error));
    }

    /**
     * Fallback method for getSimilarProductIds when circuit breaker opens or retries exhausted.
     */
    @SuppressWarnings("unused")
    private Mono<@NonNull List<String>> getSimilarProductIdsFallback(String productId, Throwable throwable) {
        LOGGER.error("Fallback triggered for getSimilarProductIds. ProductId: {}, Error: {}",
                productId, throwable.getMessage());
        return Mono.error(new ExternalServiceException(
                SERVICE_NAME,
                "Failed to retrieve similar product IDs after retries",
                throwable
        ));
    }

    /**
     * Fallback method for getProductDetail when circuit breaker opens or retries exhausted.
     */
    @SuppressWarnings("unused")
    private Mono<Product> getProductDetailFallback(String productId, Throwable throwable) {
        LOGGER.warn("Fallback triggered for getProductDetail. ProductId: {}, Error: {}",
                productId, throwable.getMessage());
        return Mono.empty();
    }
}
