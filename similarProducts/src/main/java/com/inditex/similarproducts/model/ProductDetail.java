package com.inditex.similarproducts.model;

import java.math.BigDecimal;

public record ProductDetail(
        String id,
        String name,
        BigDecimal price,
        Boolean availability
) {}
