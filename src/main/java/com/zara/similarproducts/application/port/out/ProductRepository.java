package com.zara.similarproducts.application.port.out;

import com.zara.similarproducts.domain.model.Product;
import com.zara.similarproducts.domain.model.ProductId;
import reactor.core.publisher.Mono;

public interface ProductRepository {
    
    Mono<Product> findById(ProductId productId);
}