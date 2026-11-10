package com.inditex.similarproducts.product;

import java.math.BigDecimal;

public record ProductDetail(String id, String name, BigDecimal price, Boolean availability) {
    public ProductDetail {
        if (id == null || id.isBlank() || name == null || name.isBlank()
                || price == null || availability == null) {
            throw new IllegalArgumentException("Product detail is missing required fields");
        }
    }
}
