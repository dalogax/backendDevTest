package org.challenge.products.infrastructure.dto;

import tools.jackson.databind.PropertyNamingStrategies;
import tools.jackson.databind.annotation.JsonNaming;

@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public record ProductResponseDto(
        String productId,
        String name,
        Double price,
        Boolean availability
) {}
