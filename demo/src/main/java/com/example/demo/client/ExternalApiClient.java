package com.example.demo.client;

import com.example.demo.model.Product;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Mono;
import java.time.Duration;
import java.util.List;
import org.springframework.core.ParameterizedTypeReference;

@Component
public class ExternalApiClient {

    private final WebClient webClient;
    private final Duration timeout;

    public ExternalApiClient(
            @Value("${api.base.url}") String baseUrl,
            @Value("${api.timeout.seconds}") int timeoutSeconds) {

        this.webClient = WebClient.builder()
                .baseUrl(baseUrl)
                .build();
        this.timeout = Duration.ofSeconds(timeoutSeconds);
    }

    public Mono<List<String>> getSimilarIds(String productId) {
        return webClient.get()
                .uri("/product/{id}/similarids", productId)
                .retrieve()
                .bodyToMono(String[].class) // Primero como array
                .map(array -> List.of(array)) // Luego convertir a List
                .timeout(timeout)
                .onErrorReturn(List.of());
    }

    public Mono<Product> getProductDetails(String productId) {
        return webClient.get()
                .uri("/product/{id}", productId)
                .retrieve()
                .bodyToMono(Product.class)
                .timeout(timeout)
                .onErrorResume(error -> {
                    // TODO: ELIMINAR ESTOS LOGS EN PRODUCCIÓN - Solo para debugging de prueba
                    // técnica
                    String errorMsg = "Error obteniendo producto " + productId;

                    if (error instanceof WebClientResponseException) {
                        WebClientResponseException webError = (WebClientResponseException) error;
                        errorMsg += " - Status: " + webError.getStatusCode();
                        System.err.println(errorMsg);

                        // Si es 404 o 500, es "esperado" según los mocks
                        if (webError.getStatusCode().value() == 404) {
                            System.err.println("  → Producto " + productId + " no existe (404)");
                        } else if (webError.getStatusCode().value() == 500) {
                            System.err.println("  → Error del servidor para producto " + productId + " (500)");
                        }
                    } else if (error.getMessage() != null && error.getMessage().contains("timeout")) {
                        System.err.println(errorMsg + " - TIMEOUT después de " + timeout.getSeconds() + " segundos");
                    } else {
                        System.err.println(
                                errorMsg + " - " + error.getClass().getSimpleName() + ": " + error.getMessage());
                    }

                    return Mono.empty();
                });
    }
}