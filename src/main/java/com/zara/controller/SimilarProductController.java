package com.zara.controller;

import com.zara.dto.ProductDetailDTO;
import reactor.core.publisher.Flux;

public interface SimilarProductController {
    Flux<ProductDetailDTO> getSimilarProducts(String productId);

}
