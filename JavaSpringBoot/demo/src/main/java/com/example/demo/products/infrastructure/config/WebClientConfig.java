package com.example.demo.products.infrastructure.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class WebClientConfig {

    @Value("${external.api.base-url}")
    private String baseUrl;

    @Value("${external.api.timeout.connect:2000}")
    private int connectTimeout;

    @Value("${external.api.timeout.read:5000}")
    private int readTimeout;

    @Bean
    public WebClient webClient() {
        return WebClient.builder()
                .baseUrl(baseUrl)
                .codecs(configurer -> configurer
                        .defaultCodecs()
                        .maxInMemorySize(1024 * 1024))
                .build();
    }
}

