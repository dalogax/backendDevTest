package com.dev.similarproducts.api;


import com.dev.similarproducts.dto.ProductDetailDto;
import java.util.List;

public interface ExternalApiClient {
    ProductDetailDto getProductDetail(String productId);
    List<String> getSimilarProductIds(String productId);
}
