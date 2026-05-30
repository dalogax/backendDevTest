package com.inditex.similarproducts.client;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.inditex.similarproducts.config.ProductApiProperties;
import com.inditex.similarproducts.exception.ExternalServiceException;
import com.inditex.similarproducts.exception.ProductNotFoundException;
import io.github.resilience4j.circuitbreaker.CircuitBreakerConfig;
import io.github.resilience4j.circuitbreaker.CircuitBreakerRegistry;
import io.github.resilience4j.retry.RetryConfig;
import io.github.resilience4j.retry.RetryRegistry;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.client.ClientResponse;
import org.springframework.web.reactive.function.client.ExchangeFunction;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.math.BigDecimal;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class ProductApiClientTest {

    private ExchangeFunction exchangeFunction;
    private ProductApiClient client;
    private Cache<String, List<String>> similarIdsCache;

    @BeforeEach
    void setUp() {
        exchangeFunction = mock(ExchangeFunction.class);

        WebClient webClient = WebClient.builder()
                .baseUrl("http://localhost:3001")
                .exchangeFunction(exchangeFunction)
                .build();

        similarIdsCache = Caffeine.newBuilder().maximumSize(100).build();

        CircuitBreakerRegistry cbRegistry = CircuitBreakerRegistry.of(
                CircuitBreakerConfig.custom()
                        .slidingWindowSize(10)
                        .failureRateThreshold(50)
                        .build());

        RetryRegistry retryRegistry = RetryRegistry.of(
                RetryConfig.custom()
                        .maxAttempts(1)
                        .build());

        ProductApiProperties properties = new ProductApiProperties("http://localhost:3001", 500, 2000);
        client = new ProductApiClient(webClient, properties, similarIdsCache, cbRegistry, retryRegistry);
    }

    private void mockResponse(HttpStatus status, String body) {
        ClientResponse response = ClientResponse.create(status)
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .body(body)
                .build();
        when(exchangeFunction.exchange(any())).thenReturn(Mono.just(response));
    }

    private void mockResponse(HttpStatus status) {
        ClientResponse response = ClientResponse.create(status).build();
        when(exchangeFunction.exchange(any())).thenReturn(Mono.just(response));
    }

    // --- getSimilarIds ---

    @Test
    void getSimilarIds_returnsListOfIds() {
        mockResponse(HttpStatus.OK, "[\"2\",\"3\",\"4\"]");

        StepVerifier.create(client.getSimilarIds("1"))
                .assertNext(ids -> assertThat(ids).containsExactly("2", "3", "4"))
                .verifyComplete();
    }

    @Test
    void getSimilarIds_returnsEmptyList() {
        mockResponse(HttpStatus.OK, "[]");

        StepVerifier.create(client.getSimilarIds("1"))
                .assertNext(ids -> assertThat(ids).isEmpty())
                .verifyComplete();
    }

    @Test
    void getSimilarIds_throwsProductNotFoundException_on404() {
        mockResponse(HttpStatus.NOT_FOUND);

        StepVerifier.create(client.getSimilarIds("99"))
                .expectError(ProductNotFoundException.class)
                .verify();
    }

    @Test
    void getSimilarIds_throwsExternalServiceException_on500() {
        mockResponse(HttpStatus.INTERNAL_SERVER_ERROR);

        StepVerifier.create(client.getSimilarIds("1"))
                .expectError(ExternalServiceException.class)
                .verify();
    }

    @Test
    void getSimilarIds_returnsCachedResult_onSecondCall() {
        mockResponse(HttpStatus.OK, "[\"2\",\"3\"]");

        StepVerifier.create(client.getSimilarIds("1"))
                .assertNext(ids -> assertThat(ids).containsExactly("2", "3"))
                .verifyComplete();

        StepVerifier.create(client.getSimilarIds("1"))
                .assertNext(ids -> assertThat(ids).containsExactly("2", "3"))
                .verifyComplete();

        verify(exchangeFunction, times(1)).exchange(any());
    }

    // --- getProductDetail ---

    @Test
    void getProductDetail_returnsProduct() {
        mockResponse(HttpStatus.OK,
                "{\"id\":\"2\",\"name\":\"Dress\",\"price\":19.99,\"availability\":true}");

        StepVerifier.create(client.getProductDetail("2"))
                .assertNext(product -> {
                    assertThat(product.id()).isEqualTo("2");
                    assertThat(product.name()).isEqualTo("Dress");
                    assertThat(product.price()).isEqualByComparingTo(new BigDecimal("19.99"));
                    assertThat(product.availability()).isTrue();
                })
                .verifyComplete();
    }

    @Test
    void getProductDetail_throwsProductNotFoundException_on404() {
        mockResponse(HttpStatus.NOT_FOUND);

        StepVerifier.create(client.getProductDetail("99"))
                .expectError(ProductNotFoundException.class)
                .verify();
    }

    @Test
    void getProductDetail_cachesNegativeResult_on404() {
        mockResponse(HttpStatus.NOT_FOUND);

        StepVerifier.create(client.getProductDetail("99"))
                .expectError(ProductNotFoundException.class)
                .verify();

        // Second call must not hit the upstream — the absence is cached
        StepVerifier.create(client.getProductDetail("99"))
                .expectError(ProductNotFoundException.class)
                .verify();

        verify(exchangeFunction, times(1)).exchange(any());
    }

    @Test
    void getProductDetail_throwsExternalServiceException_on500() {
        mockResponse(HttpStatus.INTERNAL_SERVER_ERROR);

        StepVerifier.create(client.getProductDetail("1"))
                .expectError(ExternalServiceException.class)
                .verify();
    }

    @Test
    void getProductDetail_returnsCachedResult_onSecondCall() {
        mockResponse(HttpStatus.OK,
                "{\"id\":\"2\",\"name\":\"Dress\",\"price\":19.99,\"availability\":true}");

        StepVerifier.create(client.getProductDetail("2"))
                .assertNext(p -> assertThat(p.id()).isEqualTo("2"))
                .verifyComplete();

        StepVerifier.create(client.getProductDetail("2"))
                .assertNext(p -> assertThat(p.id()).isEqualTo("2"))
                .verifyComplete();

        verify(exchangeFunction, times(1)).exchange(any());
    }

    @Test
    void getProductDetail_doesNotCacheErrors() {
        // First call: 500
        ClientResponse errorResponse = ClientResponse.create(HttpStatus.INTERNAL_SERVER_ERROR).build();
        // Second call: 200
        ClientResponse successResponse = ClientResponse.create(HttpStatus.OK)
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .body("{\"id\":\"2\",\"name\":\"Dress\",\"price\":19.99,\"availability\":true}")
                .build();

        when(exchangeFunction.exchange(any()))
                .thenReturn(Mono.just(errorResponse))
                .thenReturn(Mono.just(successResponse));

        StepVerifier.create(client.getProductDetail("2"))
                .expectError(ExternalServiceException.class)
                .verify();

        StepVerifier.create(client.getProductDetail("2"))
                .assertNext(p -> assertThat(p.id()).isEqualTo("2"))
                .verifyComplete();

        verify(exchangeFunction, times(2)).exchange(any());
    }
}
