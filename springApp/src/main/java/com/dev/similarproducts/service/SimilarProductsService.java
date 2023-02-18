package com.dev.similarproducts.service;

import com.dev.similarproducts.dto.ProductDetailDto;
import com.dev.similarproducts.exception.ExternalApiException;

import java.util.List;

public interface SimilarProductsService {
    List<ProductDetailDto> getSimilarProducts(String productId) throws ExternalApiException;
}
