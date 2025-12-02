package com.zara.similarproducts.domain.model;

import java.math.BigDecimal;
import java.util.Objects;

public record Product(
        ProductId id,
        String name,
        BigDecimal price,
        boolean availability
) {
    
    public Product {
        Objects.requireNonNull(id, "Product ID cannot be null");
        Objects.requireNonNull(name, "Product name cannot be null");
        Objects.requireNonNull(price, "Product price cannot be null");
        
        if (name.isBlank()) {
            throw new IllegalArgumentException("Product name cannot be blank");
        }
        if (price.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("Product price cannot be negative");
        }
    }
    
    public static Product of(ProductId id, String name, BigDecimal price, boolean availability) {
        return new Product(id, name, price, availability);
    }
}