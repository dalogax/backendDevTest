package com.nicolas_sancho.similar_products.service;

import com.nicolas_sancho.similar_products.dto.ProductDetailDTO;

import java.util.List;

public interface ProductService {
    List<ProductDetailDTO> getSimilarProducts(String productId);
}
