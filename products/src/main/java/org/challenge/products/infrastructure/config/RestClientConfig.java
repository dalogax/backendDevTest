package org.challenge.products.infrastructure.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestClient;

@Configuration
public class RestClientConfig {

    @Bean
    public RestClient productRestClient(@Value("${integrations.product-client.base-url}") String productClientBaseUrl) {
        return RestClient.builder()
                .baseUrl(productClientBaseUrl)
                .build();
    }

}
