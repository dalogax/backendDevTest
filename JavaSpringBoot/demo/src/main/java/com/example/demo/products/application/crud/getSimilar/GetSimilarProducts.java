package com.example.demo.products.application.crud.getSimilar;

import com.example.demo.products.application.dto.ProductApplication;

import java.util.List;

public interface GetSimilarProducts {
    List<ProductApplication> getSimilarProductsById(String productId);
}

