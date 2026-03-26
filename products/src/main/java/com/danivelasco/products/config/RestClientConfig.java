package com.danivelasco.products.config;

import io.netty.channel.ChannelOption;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.netty.http.client.HttpClient;
import reactor.netty.resources.ConnectionProvider;

/**
 * Configuration for WebClient used to call external APIs.
 *
 * <p>WebClient is used instead of RestTemplate or RestClient because:
 * <ul>
 *     <li>Non-blocking I/O (reactive), allowing better scalability under high concurrency</li>
 *     <li>Efficient resource usage (threads are not blocked waiting for I/O)</li>
 *     <li>Better integration with Project Reactor (Mono/Flux)</li>
 * </ul>
 *
 * <p>A custom connection pool is configured to:
 * <ul>
 *     <li>Limit the number of concurrent outbound connections</li>
 *     <li>Prevent uncontrolled resource usage</li>
 *     <li>Avoid bottlenecks under load</li>
 * </ul>
 *
 * <p>Trade-off: requires careful tuning of pool size and concurrency to avoid saturation.
 */
@Configuration
public class RestClientConfig {

    /**
     * Creates a WebClient configured with connection pooling.
     *
     * @param baseUrl        base URL of the external products API
     * @param connectTimeout connection timeout in milliseconds
     * @return configured WebClient instance
     */
    @Bean
    public WebClient productApiWebClient(
            @Value("${external.products-api.base-url}") String baseUrl,
            @Value("${external.products-api.timeouts.connect-ms}") int connectTimeout
    ) {
        ConnectionProvider provider = ConnectionProvider.builder("product-api-pool")
                .pendingAcquireMaxCount(200)
                .maxConnections(200)
                .build();

        HttpClient httpClient = HttpClient.create(provider)
                .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, connectTimeout);

        return WebClient.builder()
                .baseUrl(baseUrl)
                .clientConnector(new ReactorClientHttpConnector(httpClient))
                .build();
    }
}