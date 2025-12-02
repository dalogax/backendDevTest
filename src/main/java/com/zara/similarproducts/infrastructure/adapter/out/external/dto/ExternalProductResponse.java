package com.zara.similarproducts.infrastructure.adapter.out.external.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.math.BigDecimal;

public record ExternalProductResponse(
        @JsonProperty("id") String id,
        @JsonProperty("name") String name,
        @JsonProperty("price") BigDecimal price,
        @JsonProperty("availability") boolean availability
) {}