package com.product.service;

import com.product.dto.SimilarProductDTO;

public interface ProductService {
    
    SimilarProductDTO findSimilarProducts(String productId);
}
