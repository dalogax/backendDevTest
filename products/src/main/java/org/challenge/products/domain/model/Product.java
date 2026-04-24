package org.challenge.products.domain.model;

import java.io.Serializable;

public record Product(
        String productId,
        String name,
        Double price,
        Boolean availability
) implements Serializable {}
