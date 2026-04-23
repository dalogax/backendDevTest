package org.challenge.products.infrastructure.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.web.client.RestClient;

@Configuration
public class RestClientConfig {

    @Bean
    public RestClient productRestClient(
            @Value("${integrations.productClient.baseUrl}") String productClientBaseUrl,
            @Value("${integrations.productClient.connectTimeout}") int connectTimeout,
            @Value("${integrations.productClient.readTimeout}") int readTimeout) {

        SimpleClientHttpRequestFactory factory = new SimpleClientHttpRequestFactory();
        factory.setConnectTimeout(connectTimeout);
        factory.setReadTimeout(readTimeout);

        return RestClient.builder()
                .baseUrl(productClientBaseUrl)
                .requestFactory(factory)
                .build();
    }

}
