package com.diebuc.assessment.inditex.similarproducts.service;

import com.diebuc.assessment.inditex.similarproducts.dto.ProductDetailDTO;
import reactor.core.publisher.Flux;

public interface SimilarProductsService {
    Flux<ProductDetailDTO> getSimilarProducts(String productId);
}
