package com.capitole.similarproducts.domain.model;

import java.math.BigDecimal;
import java.util.Objects;

/**
 * Domain model representing a Product.
 */
public final record Product(String id, String name, BigDecimal price, boolean availability) {

    /**
     * Validates that the product has all required fields.
     *
     * @return true if product is valid, false otherwise
     */
    public boolean isValid() {
        return Objects.nonNull(id) && !id.isBlank() &&
            Objects.nonNull(name) && !name.isBlank() &&
            Objects.nonNull(price) && price.compareTo(BigDecimal.ZERO) >= 0;
    }

    /**
     * Creates a copy of this product with availability set to false. Useful for fallback scenarios
     * when external service fails.
     *
     * @return new Product instance with availability = false
     */
    public Product asUnavailable() {
        return new Product(this.id, this.name, this.price, false);
    }
}
