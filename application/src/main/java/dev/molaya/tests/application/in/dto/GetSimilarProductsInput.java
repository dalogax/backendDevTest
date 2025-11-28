package dev.molaya.tests.application.in.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Builder;

@Builder
public record GetSimilarProductsInput(@NotNull String productId) {}
