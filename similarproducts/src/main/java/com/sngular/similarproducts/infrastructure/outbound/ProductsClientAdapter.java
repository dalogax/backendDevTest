package com.sngular.similarproducts.infrastructure.outbound;

import java.util.Collection;
import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClient;

import com.sngular.similarproducts.application.outbound.ProductsPort;
import com.sngular.similarproducts.domain.ProductDetail;
import com.sngular.similarproducts.domain.exception.ProductNotFoundException;
import com.sngular.similarproducts.domain.exception.SimilarProductsNotFoundException;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class ProductsClientAdapter implements ProductsPort {

    private static final ParameterizedTypeReference<List<String>> STRING_LIST_TYPE = new ParameterizedTypeReference<>() {
    };

    private final RestClient restClient;

    public ProductsClientAdapter(
            RestClient.Builder restClientBuilder,
            @Value("${app.product-service.base-url}") String baseUrl) {
        this.restClient = restClientBuilder.baseUrl(baseUrl).build();
    }

    @Override
    @CircuitBreaker(name = "productsService", fallbackMethod = "getSimilarProductIdsFallback")
    @Retry(name = "productsService")
    public Collection<String> getSimilarProductIds(String productId) {
        log.debug("Calling GET /product/{}/similarids", productId);
        try {
            List<String> ids = restClient.get()
                    .uri("/product/{productId}/similarids", productId)
                    .retrieve()
                    .body(STRING_LIST_TYPE);

            return ids == null ? List.of() : ids;
        } catch (HttpClientErrorException.NotFound ex) {
            log.warn("Similar IDs not found for productId {}", productId, ex);
            throw new SimilarProductsNotFoundException(productId, ex);
        }
    }

    /**
     * Fallback method when circuit breaker is open for getSimilarProductIds.
     * Returns an empty list to allow graceful degradation.
     */
    public Collection<String> getSimilarProductIdsFallback(String productId, Exception ex) {
        log.warn("Circuit breaker open for getSimilarProductIds. Returning empty list for productId {}", productId, ex);
        return List.of();
    }

    @Override
    @CircuitBreaker(name = "productsService", fallbackMethod = "getProductFallback")
    @Retry(name = "productsService")
    public ProductDetail getProduct(String productId) {
        log.debug("Calling GET /product/{}", productId);
        try {
            return restClient.get()
                    .uri("/product/{productId}", productId)
                    .retrieve()
                    .body(ProductDetail.class);
        } catch (HttpClientErrorException.NotFound ex) {
            log.warn("Product detail not found for productId {}", productId, ex);
            throw new ProductNotFoundException(productId, ex);
        }
    }

    /**
     * Fallback method when circuit breaker is open for getProduct.
     * Throws ProductNotFoundException with a specific message about circuit breaker
     * being open.
     */
    public ProductDetail getProductFallback(String productId, Exception ex) {
        log.error("Circuit breaker open for getProduct. Service unavailable for productId {}", productId, ex);
        throw new ProductNotFoundException(productId,
                new RuntimeException("Product service unavailable - circuit breaker is open", ex));
    }

}