package com.danivelasco.products.client;

import com.danivelasco.products.dto.ProductResponse;
import reactor.core.publisher.Mono;

import java.util.List;

/**
 * Client abstraction for external product API.
 *
 * <p>This layer decouples the service logic from the HTTP client implementation,
 * making the system easier to test, maintain and evolve.
 *
 * <p>It also centralizes external communication concerns (URLs, serialization, etc.).
 */
public interface ProductApiClient {

    Mono<List<String>> getSimilarProductIds(String productId);

    Mono<ProductResponse> getProductDetail(String productId);
}
