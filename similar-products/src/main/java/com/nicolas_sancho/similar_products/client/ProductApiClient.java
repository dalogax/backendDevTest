package com.nicolas_sancho.similar_products.client;

import com.nicolas_sancho.similar_products.dto.ProductDetailDTO;

import java.util.List;

public interface ProductApiClient {
    List<String> getSimilarIds(String productId);
    ProductDetailDTO getProductDetail(String productId);
}
