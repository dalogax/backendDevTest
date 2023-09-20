package com.zara.service;

import com.zara.dto.ProductDetailDTO;
import reactor.core.publisher.Mono;

public interface ProductDetailService {
    Mono<ProductDetailDTO> getProductDetail(String id);
}
