package com.globant.interview.david.msdavidmobilephone.infrastructure.output.client;

import com.globant.interview.david.msdavidmobilephone.infrastructure.output.client.dto.ProductDetailResponse;
import io.netty.channel.ChannelOption;
import io.netty.handler.timeout.ReadTimeoutHandler;
import io.netty.handler.timeout.WriteTimeoutHandler;
import jakarta.annotation.PreDestroy;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.netty.http.client.HttpClient;
import reactor.netty.resources.ConnectionProvider;
import reactor.util.retry.Retry;

import java.time.Duration;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Component
public class WebFluxProductClient {

    private final WebClient webClient;
    private final ConnectionProvider connectionProvider;

    public WebFluxProductClient(
            @Value("${external.api.base-url:http://localhost:3001}") String baseUrl,
            @Value("${webflux.connection-pool.max-connections:500}") int maxConnections,
            @Value("${webflux.connection-pool.pending-acquire-timeout:30s}") Duration pendingAcquireTimeout,
            @Value("${webflux.timeout.connect:3s}") Duration connectTimeout,
            @Value("${webflux.timeout.read:5s}") Duration readTimeout,
            @Value("${webflux.timeout.write:5s}") Duration writeTimeout,
            @Value("${webflux.retry.max-attempts:2}") int maxRetryAttempts,
            @Value("${webflux.retry.backoff:100ms}") Duration backoff
    ) {
        this.connectionProvider = ConnectionProvider.builder("webflux-pool")
                .maxConnections(maxConnections)
                .pendingAcquireTimeout(pendingAcquireTimeout)
                .maxIdleTime(Duration.ofSeconds(20))
                .maxLifeTime(Duration.ofSeconds(60))
                .build();

        HttpClient httpClient = HttpClient.create(connectionProvider)
                .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, (int) connectTimeout.toMillis())
                .responseTimeout(readTimeout)
                .doOnConnected(conn -> conn
                        .addHandlerLast(new ReadTimeoutHandler(readTimeout.toMillis(), TimeUnit.MILLISECONDS))
                        .addHandlerLast(new WriteTimeoutHandler(writeTimeout.toMillis(), TimeUnit.MILLISECONDS))
                );

        this.webClient = WebClient.builder()
                .baseUrl(baseUrl)
                .clientConnector(new ReactorClientHttpConnector(httpClient))
                .build();

        this.maxRetryAttempts = maxRetryAttempts;
        this.backoff = backoff;
    }

    private final int maxRetryAttempts;
    private final Duration backoff;

    public Flux<String> getSimilarIds(String productId) {
        return webClient.get()
                .uri("/product/{productId}/similarids", productId)
                .retrieve()
                .onStatus(HttpStatusCode::is4xxClientError, response ->
                        Mono.empty())
                .bodyToMono(new ParameterizedTypeReference<List<String>>() {})
                .retryWhen(Retry.backoff(maxRetryAttempts, backoff)
                        .filter(this::isRetryable))
                .flatMapMany(Flux::fromIterable);
    }

    public Mono<ProductDetailResponse> getProductDetail(String productId) {
        return webClient.get()
                .uri("/product/{productId}", productId)
                .retrieve()
                .onStatus(HttpStatusCode::is4xxClientError, response -> Mono.empty())
                .bodyToMono(ProductDetailResponse.class)
                .retryWhen(Retry.backoff(maxRetryAttempts, backoff)
                        .filter(throwable -> isRetryable(throwable)));
    }

    public Flux<ProductDetailResponse> getProductDetails(List<String> productIds) {
        return Flux.fromIterable(productIds)
                .flatMap(this::getProductDetail, 4);
    }

    private boolean isRetryable(Throwable throwable) {
        if (throwable instanceof WebClientResponseException webClientEx) {
            return webClientEx.getStatusCode().is5xxServerError();
        }
        return true;
    }

    @PreDestroy
    public void cleanup() {
        if (connectionProvider != null) {
            connectionProvider.dispose();
        }
    }
}
