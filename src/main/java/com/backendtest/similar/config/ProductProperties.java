package com.backendtest.similar.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "app.product")
public record ProductProperties(
        String baseUrl,
        long timeoutMs,
        long connectTimeoutMs,
        int maxConnections,
        long cacheTtlSeconds,
        long cacheMaxSize) {}
