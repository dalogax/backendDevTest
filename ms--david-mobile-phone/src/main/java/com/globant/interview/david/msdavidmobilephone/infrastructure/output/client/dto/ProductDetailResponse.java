package com.globant.interview.david.msdavidmobilephone.infrastructure.output.client.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.math.BigDecimal;

@JsonIgnoreProperties(ignoreUnknown = true)
public record ProductDetailResponse(

    @JsonProperty("id")
    String id,

    @JsonProperty("name")
    String name,

    @JsonProperty("price")
    BigDecimal price,

    @JsonProperty("availability")
    boolean availability
) {}
