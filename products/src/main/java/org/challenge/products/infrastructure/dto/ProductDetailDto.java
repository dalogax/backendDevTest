package org.challenge.products.infrastructure.dto;

import java.io.Serializable;

public record ProductDetailDto (
        String id,
        String name,
        Double price,
        Boolean availability
) implements Serializable {}
