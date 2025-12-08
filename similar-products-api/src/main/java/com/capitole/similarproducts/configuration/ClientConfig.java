package com.capitole.similarproducts.configuration;

import com.capitole.similarproducts.adapter.out.client.ProductHttpApi;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.support.WebClientAdapter;
import org.springframework.web.service.invoker.HttpServiceProxyFactory;
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
    public ProductHttpApi productHttpApi(WebClient.Builder webClientBuilder,
                                         @Value("${external.product-service.base-url}") String baseUrl) {
        LOGGER.info("Initializing ProductHttpApi with base URL: {} (using OpenAPI models)", baseUrl);
        
        ConnectionProvider provider = ConnectionProvider.builder("custom")
            .maxConnections(500)
            .pendingAcquireMaxCount(1000)
            .build();

        HttpClient httpClient = HttpClient.create(provider)
            .compress(true)
            .keepAlive(true);

        WebClient webClient = webClientBuilder
            .baseUrl(baseUrl)
            .clientConnector(new ReactorClientHttpConnector(httpClient))
            .build();
            
        WebClientAdapter adapter = WebClientAdapter.create(webClient);
        HttpServiceProxyFactory factory = HttpServiceProxyFactory.builderFor(adapter).build();
        return factory.createClient(ProductHttpApi.class);
    }
}
