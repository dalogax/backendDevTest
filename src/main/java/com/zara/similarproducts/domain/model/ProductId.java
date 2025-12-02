package com.zara.similarproducts.domain.model;

import java.util.Objects;

public record ProductId(String value) {
    
    public ProductId {
        Objects.requireNonNull(value, "Product ID cannot be null");
        if (value.isBlank()) {
            throw new InvalidProductIdException(value);
        }
        if (!value.matches("^[0-9]+$")) {
            throw new InvalidProductIdException(value);
        }
    }
    
    public static ProductId of(String value) {
        return new ProductId(value);
    }
}