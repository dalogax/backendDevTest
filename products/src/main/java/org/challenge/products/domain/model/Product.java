package org.challenge.products.domain.model;

public record Product(
        String productId,
        String name,
        Double price,
        Boolean availability
) {}
