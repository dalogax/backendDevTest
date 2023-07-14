package com.alejandro.api.service;

import com.alejandro.api.dto.ProductDetailDTO;

import java.util.List;
import java.util.Optional;

public interface IProductsService {

    Optional<List<String>> getSimilarProductIds(String productId);

    Optional<ProductDetailDTO> getProductDetail(String productId);
}
