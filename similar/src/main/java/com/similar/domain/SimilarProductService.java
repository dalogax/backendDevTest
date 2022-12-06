package com.similar.domain;

import com.similar.domain.model.Product;

import java.util.List;

public interface SimilarProductService {
    List<Product> getSimilarProductBy(Long productId);
}
