package com.capitole.similarproducts.adapter.in.rest.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.math.BigDecimal;

/**
 * REST API DTO for product information.
 */
public record ProductDto(
    @JsonProperty("id") String id,
    @JsonProperty("name") String name,
    @JsonProperty("price") BigDecimal price,
    @JsonProperty("availability") boolean availability
) {}
