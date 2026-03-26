package com.danivelasco.products.service;

import com.danivelasco.products.dto.ProductDetailsResponse;
import com.danivelasco.products.dto.ProductResponse;
import reactor.core.publisher.Mono;

import java.util.List;

public interface SimilarProductService {

    Mono<ProductDetailsResponse> getSimilarProducts(String productId);

}
