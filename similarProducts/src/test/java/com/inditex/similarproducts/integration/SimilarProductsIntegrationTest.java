package com.inditex.similarproducts.integration;

import com.inditex.similarproducts.client.ProductApiClient;
import com.inditex.similarproducts.exception.ExternalServiceException;
import com.inditex.similarproducts.exception.ProductNotFoundException;
import com.inditex.similarproducts.model.ProductDetail;
import io.github.resilience4j.circuitbreaker.CallNotPermittedException;
import io.github.resilience4j.circuitbreaker.CircuitBreaker;
import io.github.resilience4j.circuitbreaker.CircuitBreakerRegistry;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.web.reactive.function.client.WebClientRequestException;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;
import java.net.URI;
import java.util.List;
import java.util.concurrent.TimeoutException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
@AutoConfigureWebTestClient
class SimilarProductsIntegrationTest {

    @MockBean
    private ProductApiClient productApiClient;

    @Autowired
    private WebTestClient webTestClient;

    private static final ProductDetail DRESS = new ProductDetail("2", "Dress", new BigDecimal("19.99"), true);
    private static final ProductDetail BLAZER = new ProductDetail("3", "Blazer", new BigDecimal("29.99"), false);
    private static final ProductDetail BOOTS = new ProductDetail("4", "Boots", new BigDecimal("39.99"), true);

    @Test
    void returns200WithAllProducts_happyPath() {
        when(productApiClient.getSimilarIds("1")).thenReturn(Mono.just(List.of("2", "3", "4")));
        when(productApiClient.getProductDetail("2")).thenReturn(Mono.just(DRESS));
        when(productApiClient.getProductDetail("3")).thenReturn(Mono.just(BLAZER));
        when(productApiClient.getProductDetail("4")).thenReturn(Mono.just(BOOTS));

        webTestClient.get().uri("/product/1/similar")
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(ProductDetail.class)
                .hasSize(3);
    }

    @Test
    void returns404_whenSimilarIdsNotFound() {
        when(productApiClient.getSimilarIds("99"))
                .thenReturn(Mono.error(new ProductNotFoundException("99")));

        webTestClient.get().uri("/product/99/similar")
                .exchange()
                .expectStatus().isNotFound()
                .expectBody()
                .jsonPath("$.code").isEqualTo("PRODUCT_NOT_FOUND");
    }

    @Test
    void returns200WithPartialList_whenOneDetailReturns404() {
        when(productApiClient.getSimilarIds("1")).thenReturn(Mono.just(List.of("2", "5")));
        when(productApiClient.getProductDetail("2")).thenReturn(Mono.just(DRESS));
        when(productApiClient.getProductDetail("5"))
                .thenReturn(Mono.error(new ProductNotFoundException("5")));

        webTestClient.get().uri("/product/1/similar")
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(ProductDetail.class)
                .hasSize(1)
                .value(products -> assertThat(products.get(0).id()).isEqualTo("2"));
    }

    @Test
    void returns200WithPartialList_whenOneDetailReturns500() {
        when(productApiClient.getSimilarIds("1")).thenReturn(Mono.just(List.of("2", "6")));
        when(productApiClient.getProductDetail("2")).thenReturn(Mono.just(DRESS));
        when(productApiClient.getProductDetail("6"))
                .thenReturn(Mono.error(new RuntimeException("upstream 500")));

        webTestClient.get().uri("/product/1/similar")
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(ProductDetail.class)
                .hasSize(1);
    }

    @Test
    void returns200WithEmptyList_whenNoSimilarIds() {
        when(productApiClient.getSimilarIds("1")).thenReturn(Mono.just(List.of()));

        webTestClient.get().uri("/product/1/similar")
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(ProductDetail.class)
                .hasSize(0);
    }

    @Test
    void returnsCorrectJsonSchema_forProductDetail() {
        when(productApiClient.getSimilarIds("1")).thenReturn(Mono.just(List.of("2")));
        when(productApiClient.getProductDetail("2")).thenReturn(Mono.just(DRESS));

        webTestClient.get().uri("/product/1/similar")
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$[0].id").isEqualTo("2")
                .jsonPath("$[0].name").isEqualTo("Dress")
                .jsonPath("$[0].price").isEqualTo(19.99)
                .jsonPath("$[0].availability").isEqualTo(true);
    }

    @Test
    void returns502_whenUpstreamIsUnreachable() {
        when(productApiClient.getSimilarIds("1"))
                .thenReturn(Mono.error(new WebClientRequestException(
                        new java.net.ConnectException("Connection refused"),
                        org.springframework.http.HttpMethod.GET,
                        URI.create("http://localhost:3001/product/1/similarids"),
                        org.springframework.http.HttpHeaders.EMPTY)));

        webTestClient.get().uri("/product/1/similar")
                .exchange()
                .expectStatus().isEqualTo(502)
                .expectBody()
                .jsonPath("$.code").isEqualTo("EXTERNAL_SERVICE_UNAVAILABLE");
    }

    @Test
    void returns504_whenUpstreamTimesOut() {
        when(productApiClient.getSimilarIds("1"))
                .thenReturn(Mono.error(new TimeoutException("Read timeout after 500ms")));

        webTestClient.get().uri("/product/1/similar")
                .exchange()
                .expectStatus().isEqualTo(504)
                .expectBody()
                .jsonPath("$.code").isEqualTo("UPSTREAM_TIMEOUT");
    }

    @Test
    void returns503_whenCircuitBreakerIsOpen() {
        CircuitBreaker cb = CircuitBreakerRegistry.ofDefaults().circuitBreaker("test");
        when(productApiClient.getSimilarIds("1"))
                .thenReturn(Mono.error(CallNotPermittedException.createCallNotPermittedException(cb)));

        webTestClient.get().uri("/product/1/similar")
                .exchange()
                .expectStatus().isEqualTo(503)
                .expectBody()
                .jsonPath("$.code").isEqualTo("SERVICE_UNAVAILABLE");
    }

    @Test
    void returns502_whenExternalServiceError() {
        when(productApiClient.getSimilarIds("1"))
                .thenReturn(Mono.error(new ExternalServiceException("Upstream 500")));

        webTestClient.get().uri("/product/1/similar")
                .exchange()
                .expectStatus().isEqualTo(502)
                .expectBody()
                .jsonPath("$.code").isEqualTo("EXTERNAL_SERVICE_ERROR");
    }

    @Test
    void returns502_whenWebClientResponseError() {
        when(productApiClient.getSimilarIds("1"))
                .thenReturn(Mono.error(WebClientResponseException.create(
                        HttpStatus.INTERNAL_SERVER_ERROR.value(), "Internal Server Error",
                        org.springframework.http.HttpHeaders.EMPTY, new byte[0], null)));

        webTestClient.get().uri("/product/1/similar")
                .exchange()
                .expectStatus().isEqualTo(502)
                .expectBody()
                .jsonPath("$.code").isEqualTo("EXTERNAL_SERVICE_ERROR");
    }

    @Test
    void returns500_whenUnexpectedError() {
        when(productApiClient.getSimilarIds("1"))
                .thenReturn(Mono.error(new IllegalStateException("Something unexpected")));

        webTestClient.get().uri("/product/1/similar")
                .exchange()
                .expectStatus().isEqualTo(500)
                .expectBody()
                .jsonPath("$.code").isEqualTo("INTERNAL_ERROR");
    }
}
