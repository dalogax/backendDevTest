package com.inditex.similarproducts.config;

import io.github.resilience4j.circuitbreaker.CircuitBreaker;
import io.github.resilience4j.circuitbreaker.CircuitBreakerRegistry;
import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.ReactiveHealthIndicator;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.time.Duration;

@Component
public class UpstreamHealthIndicator implements ReactiveHealthIndicator {

    private final WebClient webClient;
    private final CircuitBreaker circuitBreaker;

    public UpstreamHealthIndicator(WebClient productWebClient, CircuitBreakerRegistry registry) {
        this.webClient = productWebClient;
        this.circuitBreaker = registry.circuitBreaker("productClient");
    }

    @Override
    public Mono<Health> health() {
        CircuitBreaker.State cbState = circuitBreaker.getState();
        Health.Builder builder = (cbState == CircuitBreaker.State.OPEN)
                ? Health.down()
                : Health.up();

        builder.withDetail("circuitBreaker", cbState.name());

        return webClient.get()
                .uri("/product/1/similarids")
                .retrieve()
                .toBodilessEntity()
                .timeout(Duration.ofSeconds(2))
                .map(response -> builder
                        .withDetail("upstream", "reachable")
                        .build())
                .onErrorResume(ex -> Mono.just(builder
                        .down()
                        .withDetail("upstream", "unreachable")
                        .withDetail("error", ex.getMessage())
                        .build()));
    }
}
