package dev.molaya.tests.application.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.bind.DefaultValue;

@ConfigurationProperties(prefix = "config.similar-products")
public record SimilarProductsConfigurationProperties(
        @DefaultValue("20") int limit, @DefaultValue("20") int parallelRequests) {}
