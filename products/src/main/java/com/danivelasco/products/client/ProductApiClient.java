package com.danivelasco.products.client;

import com.danivelasco.products.dto.ProductResponse;
import reactor.core.publisher.Mono;

import java.util.List;

public interface ProductApiClient {

    Mono<List<String>> getSimilarProductIds(String productId);

    Mono<ProductResponse> getProductDetail(String productId);
}
