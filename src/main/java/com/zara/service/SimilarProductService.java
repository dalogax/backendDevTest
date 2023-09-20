package com.zara.service;

import com.zara.dto.ProductDetailDTO;
import reactor.core.publisher.Flux;

public interface SimilarProductService {
    Flux<ProductDetailDTO> getSimilarProducts(String productId);
}
