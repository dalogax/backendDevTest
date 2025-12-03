package com.zara.similarproducts.application.port.out;

import com.zara.similarproducts.domain.model.ProductId;
import reactor.core.publisher.Flux;

public interface SimilarProductsRepository {
    
    Flux<ProductId> findSimilarProductIds(ProductId productId);
}