package com.inditex.similarproducts.catalog;

import java.net.URI;
import java.time.Duration;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

@Validated
@ConfigurationProperties("catalog")
public record CatalogProperties(
        @NotNull URI baseUrl,
        @NotNull Duration connectTimeout,
        @NotNull Duration requestTimeout,
        @NotNull Duration maxIdleTime,
        @Min(1) int maxConcurrency,
        @Min(1) int maxConnections,
        @Min(1) int pendingAcquireMaxCount) {

    public CatalogProperties {
        if (baseUrl != null && (!baseUrl.isAbsolute() || baseUrl.getHost() == null
                || !("http".equals(baseUrl.getScheme()) || "https".equals(baseUrl.getScheme())))) {
            throw new IllegalArgumentException("catalog.base-url must be an absolute HTTP(S) URL");
        }
        if (connectTimeout != null && (connectTimeout.toMillis() < 1
                || connectTimeout.toMillis() > Integer.MAX_VALUE)) {
            throw new IllegalArgumentException("catalog.connect-timeout must be between 1ms and 2147483647ms");
        }
        if (requestTimeout != null && requestTimeout.toMillis() < 1) {
            throw new IllegalArgumentException("catalog.request-timeout must be at least 1ms");
        }
        if (maxIdleTime != null && maxIdleTime.toMillis() < 1) {
            throw new IllegalArgumentException("catalog.max-idle-time must be at least 1ms");
        }
    }
}
