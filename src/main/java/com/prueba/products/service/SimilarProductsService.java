package com.prueba.products.service;

import com.prueba.products.model.Product;

import java.util.List;
public interface SimilarProductsService {
    List<Product> getSimilarProducts(String productId);
}
