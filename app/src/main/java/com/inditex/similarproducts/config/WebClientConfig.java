package com.inditex.similarproducts.config;

import io.netty.channel.ChannelOption;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.netty.http.client.HttpClient;
import reactor.netty.resources.ConnectionProvider;

import java.time.Duration;

@Configuration
public class WebClientConfig {

    @Bean
    public WebClient productWebClient(
            @Value("${product-api.base-url}") String baseUrl,
            @Value("${product-api.connect-timeout-ms:1000}") int connectTimeoutMs,
            @Value("${product-api.max-connections:50}") int maxConnections,
            @Value("${product-api.pending-acquire-timeout-ms:2000}") long pendingAcquireTimeoutMs) {

        // Explicit, modest pool for deterministic throughput across machines (the reactor-netty
        // default is CPU-dependent: max(cores, 8) * 2). Load testing showed the upstream is the
        // bottleneck, not this pool: oversizing floods the single-process mock and degrades latency,
        // while undersizing silently drops available products under load. maxConnections is the main
        // performance lever and sits on a throughput/completeness Pareto frontier; 50 is the balanced
        // default. Tune per environment via PRODUCT_API_MAX_CONNECTIONS — see app/README.md
        // ("Performance analysis & tuning") for the data and the max-throughput / max-correctness
        // presets. pendingAcquireTimeout is aligned with the request timeout budget so a request
        // never waits for a connection longer than it would wait for a response.
        ConnectionProvider connectionProvider = ConnectionProvider.builder("product-api")
                .maxConnections(maxConnections)
                .pendingAcquireTimeout(Duration.ofMillis(pendingAcquireTimeoutMs))
                .build();

        HttpClient httpClient = HttpClient.create(connectionProvider)
                .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, connectTimeoutMs);

        return WebClient.builder()
                .baseUrl(baseUrl)
                .clientConnector(new ReactorClientHttpConnector(httpClient))
                .build();
    }
}
