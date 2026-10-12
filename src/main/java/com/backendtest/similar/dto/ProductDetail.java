package com.backendtest.similar.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;

public record ProductDetail(
        @NotBlank String id,
        @NotBlank String name,
        @NotNull @DecimalMin("0.0") BigDecimal price,
        @NotNull Boolean availability) {}
