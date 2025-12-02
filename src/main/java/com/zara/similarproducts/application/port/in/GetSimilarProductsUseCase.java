package com.zara.similarproducts.application.port.in;

import com.zara.similarproducts.domain.model.ProductId;
import com.zara.similarproducts.domain.model.SimilarProducts;
import reactor.core.publisher.Mono;

public interface GetSimilarProductsUseCase {
    
    Mono<SimilarProducts> execute(ProductId productId);
}