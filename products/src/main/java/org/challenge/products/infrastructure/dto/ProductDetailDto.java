package org.challenge.products.infrastructure.dto;

public record ProductDetailDto (
        String id,
        String name,
        Double price,
        Boolean availability
) {}
