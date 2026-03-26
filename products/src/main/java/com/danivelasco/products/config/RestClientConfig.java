package com.danivelasco.products.config;

import io.netty.channel.ChannelOption;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.netty.http.client.HttpClient;
import reactor.netty.resources.ConnectionProvider;

@Configuration
public class RestClientConfig {

    @Bean
    public WebClient productApiWebClient(
            @Value("${external.products-api.base-url}") String baseUrl,
            @Value("${external.products-api.timeouts.connect-ms}") int connectTimeout
    ) {
        ConnectionProvider provider = ConnectionProvider.builder("product-api-pool")
                .maxConnections(200)
                .build();

        HttpClient httpClient = HttpClient.create(provider).option(ChannelOption.CONNECT_TIMEOUT_MILLIS, connectTimeout);

        return WebClient.builder()
                .baseUrl(baseUrl)
                .clientConnector(new ReactorClientHttpConnector(httpClient))
                .build();
    }
}
