package com.inditex.similarproducts.config;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

@ConfigurationProperties(prefix = "product-api")
@Validated
public record ProductApiProperties(
        @NotBlank String baseUrl,
        @Positive int connectTimeoutMs,
        @Positive int readTimeoutMs
) {}
