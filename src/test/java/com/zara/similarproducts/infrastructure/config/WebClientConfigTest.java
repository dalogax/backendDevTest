package com.zara.similarproducts.infrastructure.config;

import org.junit.jupiter.api.Test;
import org.springframework.web.reactive.function.client.WebClient;

import static org.assertj.core.api.Assertions.assertThat;

class WebClientConfigTest {

    @Test
    void shouldCreateWebClientWithDefaultBaseUrl() {
        // Given
        WebClientConfig config = new WebClientConfig();
        String baseUrl = "http://localhost:3001";
        
        // When
        WebClient webClient = config.webClient(baseUrl);
        
        // Then
        assertThat(webClient).isNotNull();
    }

    @Test
    void shouldCreateWebClientWithCustomBaseUrl() {
        // Given
        WebClientConfig config = new WebClientConfig();
        String customBaseUrl = "http://custom-api:8080";
        
        // When
        WebClient webClient = config.webClient(customBaseUrl);
        
        // Then
        assertThat(webClient).isNotNull();
    }
}