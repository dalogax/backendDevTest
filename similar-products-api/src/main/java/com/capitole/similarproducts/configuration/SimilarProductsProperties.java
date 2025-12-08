package com.capitole.similarproducts.configuration;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Configuration properties for Similar Products application.
 */
@ConfigurationProperties(prefix = "similar-products")
public class SimilarProductsProperties {

    /**
     * Maximum concurrent calls to external product service for details.
     */
    private int maxConcurrentCalls = 10;

    public int getMaxConcurrentCalls() {
        return maxConcurrentCalls;
    }

    public void setMaxConcurrentCalls(int maxConcurrentCalls) {
        this.maxConcurrentCalls = maxConcurrentCalls;
    }
}
