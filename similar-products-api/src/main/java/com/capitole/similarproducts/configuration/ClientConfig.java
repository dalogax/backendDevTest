package com.capitole.similarproducts.configuration;

import com.capitole.similarproducts.generated.api.ProductApi;
import com.capitole.similarproducts.generated.invoker.ApiClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.netty.http.client.HttpClient;
import reactor.netty.resources.ConnectionProvider;

@Configuration
public class ClientConfig {

    private static final Logger LOGGER = LoggerFactory.getLogger(ClientConfig.class);

    @Bean
    public WebClient.Builder webClientBuilder() {
        return WebClient.builder();
    }

    @Bean
    public ProductApi productApi(@Value("${external.product-service.base-url}") String baseUrl) {
        LOGGER.info("Initializing ProductApi (generated) with base URL: {}", baseUrl);

        ConnectionProvider provider = ConnectionProvider.builder("custom")
            .maxConnections(500)
            .pendingAcquireMaxCount(1000)
            .build();

        HttpClient httpClient = HttpClient.create(provider)
            .compress(true)
            .keepAlive(true);

        WebClient webClient = WebClient.builder()
            .baseUrl(baseUrl)
            .clientConnector(new ReactorClientHttpConnector(httpClient))
            .build();

        ApiClient apiClient = new ApiClient(webClient);
        apiClient.setBasePath(baseUrl);
        return new ProductApi(apiClient);
    }
}
