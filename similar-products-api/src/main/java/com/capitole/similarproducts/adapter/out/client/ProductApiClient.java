package com.capitole.similarproducts.adapter.out.client;

import com.capitole.similarproducts.generated.api.ProductApi;

import com.capitole.similarproducts.adapter.out.client.mapper.ProductClientMapper;
import com.capitole.similarproducts.domain.exception.ExternalServiceException;
import com.capitole.similarproducts.domain.model.Product;
import com.capitole.similarproducts.domain.port.out.ProductServicePort;
import io.github.resilience4j.bulkhead.annotation.Bulkhead;
import io.github.resilience4j.bulkhead.annotation.Bulkhead.Type;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import java.util.List;
import org.jspecify.annotations.NonNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;

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
    @Bulkhead(name = "productService", type = Type.SEMAPHORE)
    @Retry(name = RETRY_NAME, fallbackMethod = "getSimilarProductIdsFallback")
    public @NonNull List<String> getSimilarProductIds(String productId) {
        LOGGER.debug("Fetching similar product IDs for productId: {}", productId);

        try {
            List<String> ids = new java.util.ArrayList<>(productApi.getProductSimilarids(productId));
            LOGGER.debug("Retrieved {} similar product IDs for productId: {}", ids.size(), productId);
            return ids;
        } catch (HttpClientErrorException.NotFound e) {
            LOGGER.warn("Product not found: {}", productId);
            throw new com.capitole.similarproducts.domain.exception.ProductNotFoundException(productId, e);
        } catch (Exception e) {
            LOGGER.error("Error fetching similar product IDs for productId: {}", productId, e);
            throw e;
        }
    }

    /**
     * Retrieves product details from external service.
     * Applies circuit breaker and retry patterns for resilience.
     *
     * @param productId the product ID
     * @return product details, or throw exception if not found
     */
    @Override
    @Cacheable(value = "products", key = "#productId")
    @CircuitBreaker(name = CIRCUIT_BREAKER_NAME, fallbackMethod = "getProductDetailFallback")
    @Bulkhead(name = "productService", type = Type.SEMAPHORE)
    @Retry(name = RETRY_NAME, fallbackMethod = "getProductDetailFallback")
    public @NonNull Product getProductDetail(String productId) {
        LOGGER.debug("Fetching product detail for productId: {}", productId);

        try {
            Product product = productClientMapper.toDomain(productApi.getProductProductId(productId));
            LOGGER.debug("Retrieved product detail for productId: {}", productId);
            return product;
        } catch (HttpClientErrorException.NotFound e) {
            LOGGER.debug("Product not found: {}", productId);
            throw new com.capitole.similarproducts.domain.exception.ProductNotFoundException(productId, e);
        } catch (Exception e) {
            LOGGER.error("Error fetching product detail for productId: {}", productId, e);
            throw e;
        }
    }

    /**
     * Fallback method for getSimilarProductIds when circuit breaker opens or retries exhausted.
     */
    @SuppressWarnings("unused")
    private @NonNull List<String> getSimilarProductIdsFallback(String productId, Throwable throwable) {
        LOGGER.error("Fallback triggered for getSimilarProductIds. ProductId: {}, Error: {}",
                productId, throwable.getMessage());
        throw new ExternalServiceException(
                SERVICE_NAME,
                "Failed to retrieve similar product IDs after retries",
                throwable
        );
    }

    /**
     * Fallback method for getProductDetail when circuit breaker opens or retries exhausted.
     */
    @SuppressWarnings("unused")
    private Product getProductDetailFallback(String productId, Throwable throwable) {
        LOGGER.warn("Fallback triggered for getProductDetail. ProductId: {}, Error: {}",
                productId, throwable.getMessage());
        // For product detail, we might want to return null to indicate missing details (which are skipped by service)
        // Or rethrow. Service expects this method to return Product or throw.
        // Wait, the interface says @NonNull Product. So I must throw or return a valid product.
        // The service catches exceptions and returns null/empty. So throwing is fine.
        throw new RuntimeException("Fallback: Failed to fetch product details", throwable);
    }
}
