package org.challenge.products.infrastructure.dto;

public record ProductDetailDto (
        String productId,
        String name,
        Double price,
        Boolean availability
) {}
