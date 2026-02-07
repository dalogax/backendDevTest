package com.globant.interview.david.msdavidmobilephone.domain.model;

import java.math.BigDecimal;

public record Product(
    String id,
    String name,
    BigDecimal price,
    boolean availability
) {
    public Product {
        if (id == null || id.isBlank()) {
            throw new IllegalArgumentException("Product id cannot be null or blank");
        }
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("Product name cannot be null or blank");
        }
        if (price == null || price.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("Product price must be positive");
        }
    }
}
