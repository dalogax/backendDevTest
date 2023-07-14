package com.alejandro.api.repository;

import com.alejandro.api.model.ProductDetailEntity;

import java.util.List;
import java.util.Optional;

public interface IProductsRepository {

    Optional<List<String>> getSimilarProductIds(String productId);

    Optional<ProductDetailEntity> getProductDetail(String productId);
}
