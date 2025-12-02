package com.zara.similarproducts.domain.model;

import java.util.List;
import java.util.Objects;

public record SimilarProducts(List<Product> products) {
    
    public SimilarProducts {
        Objects.requireNonNull(products, "Products list cannot be null");
    }
    
    public static SimilarProducts of(List<Product> products) {
        Objects.requireNonNull(products, "Products list cannot be null");
        return new SimilarProducts(List.copyOf(products));
    }
    
    public static SimilarProducts empty() {
        return new SimilarProducts(List.of());
    }
    
    public boolean isEmpty() {
        return products.isEmpty();
    }
    
    public int size() {
        return products.size();
    }
}