package com.danivelasco.products.dto;

public record ProductResponse(
        String id,
        String name,
        double price,
        boolean availability
) {
}
