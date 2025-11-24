package com.example.demo.products.infrastructure.http_outputAdapter;

import com.example.demo.products.application.dto.ProductApplication;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.util.retry.Retry;

import java.time.Duration;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class ProductApiClient {

    private final WebClient webClient;

    @Value("${external.api.timeout.read:5000}")
    private int readTimeout;


    //Spring WebFlux -> reactivo -> usar Mono -> queremos 1 unico valor que es la lista
    public Mono<List<String>> getSimilarProductIds(String productId) {
        log.debug("Fetching similar product IDs for productId: {}", productId);
        return webClient.get()
                .uri("/product/{productId}/similarids", productId)
                .retrieve()
                .bodyToFlux(String.class)
                .collectList()
                .timeout(Duration.ofMillis(readTimeout))
                .retryWhen(Retry.backoff(2, Duration.ofMillis(100))
                        .filter(throwable -> {
                            if (throwable instanceof WebClientResponseException webClientError) {
                                return webClientError.getStatusCode().is5xxServerError();
                            }
                            return false;
                        })
                        .doBeforeRetry(retrySignal -> log.warn("Retrying request for similar IDs, productId: {}", productId)))
                .doOnSuccess(ids -> log.debug("Successfully fetched {} similar product IDs for productId: {}", ids.size(), productId))
                .doOnError(error -> log.error("Error fetching similar product IDs for productId: {}", productId, error))
                .onErrorMap(this::mapToRuntimeException);
    }

    //Spring WebFlux -> reactivo -> usar Mono -> queremos 1 unico valor
    public Mono<ProductApplication> getProductDetail(String productId) {
        log.debug("Fetching product detail for productId: {}", productId);
        return webClient.get()
                .uri("/product/{productId}", productId)
                .retrieve()
                .bodyToMono(ProductApplication.class)
                .timeout(Duration.ofMillis(readTimeout))
                .retryWhen(Retry.backoff(2, Duration.ofMillis(100))
                        .filter(throwable -> {
                            if (throwable instanceof WebClientResponseException webClientError) {
                                return webClientError.getStatusCode().is5xxServerError();
                            }
                            return false;
                        })
                        .doBeforeRetry(retrySignal -> log.warn("Retrying request for product detail, productId: {}", productId)))
                .doOnSuccess(product -> log.debug("Successfully fetched product detail for productId: {}", productId))
                .doOnError(error -> log.error("Error fetching product detail for productId: {}", productId, error))
                .onErrorMap(this::mapToRuntimeException);
    }

    //Spring WebFlux -> reactivo -> usar Flux -> queremos multiples valores distintos
    public Flux<ProductApplication> getProductDetails(List<String> productIds) {
        log.debug("Fetching product details for {} products", productIds.size());
        return Flux.fromIterable(productIds)
                .flatMap(this::getProductDetail, 5) // Concurrencia de 5
                .onErrorContinue((error, obj) -> log.warn("Failed to fetch product detail for: {}", obj, error));
    }

    //Centralizar logica para manejar excepciones
    private Throwable mapToRuntimeException(Throwable error) {
        if (error instanceof WebClientResponseException webClientError) {
            if (webClientError.getStatusCode() == HttpStatus.NOT_FOUND) {
                log.warn("Product not found in external API: {}", webClientError.getMessage());
                return new RuntimeException("Product not found in external API: " + webClientError.getMessage());
            }
            return new RuntimeException("External API error: " + webClientError.getStatusCode(), error);
        }
        if (error instanceof java.util.concurrent.TimeoutException) {
            log.warn("Timeout calling external API: {}", error.getMessage());
            return new RuntimeException("External API timeout", error);
        }
        return new RuntimeException("Error calling external API", error);
    }
}

