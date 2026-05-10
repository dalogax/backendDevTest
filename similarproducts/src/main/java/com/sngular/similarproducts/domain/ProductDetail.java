package com.sngular.similarproducts.domain;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record ProductDetail(
        @NotBlank @Size(min = 1) String id,
        @NotBlank @Size(min = 1) String name,
        @NotNull Double price,
        @NotNull Boolean availability) {
}