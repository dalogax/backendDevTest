package com.danivelasco.products.client;

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

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ProductApiClientImplTest {

    private ProductApiClientImpl productApiClient;

    @BeforeEach
    void setUp() {
    }

    @Test
    void getSimilarProductIds() {
        ExchangeFunction exchangeFunction = request -> {
            assertEquals("/product/1/similarids", request.url().getPath());

            return Mono.just(
                    ClientResponse.create(HttpStatus.OK)
                            .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                            .body("[\"2\",\"3\",\"4\"]")
                            .build()
            );
        };

        WebClient webClient = WebClient.builder()
                .exchangeFunction(exchangeFunction)
                .build();

        productApiClient = new ProductApiClientImpl(webClient);

        StepVerifier.create(productApiClient.getSimilarProductIds("1"))
                .assertNext(ids -> assertEquals(List.of("2", "3", "4"), ids))
                .verifyComplete();
    }

    @Test
    void getProductDetail() {
        ExchangeFunction exchangeFunction = request -> {
            assertEquals("/product/2", request.url().getPath());

            return Mono.just(
                    ClientResponse.create(HttpStatus.OK)
                            .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                            .body("""
                                  {
                                    "id":"2",
                                    "name":"Shirt",
                                    "price":35.99,
                                    "availability":true
                                  }
                                  """)
                            .build()
            );
        };

        WebClient webClient = WebClient.builder()
                .exchangeFunction(exchangeFunction)
                .build();

        productApiClient = new ProductApiClientImpl(webClient);

        StepVerifier.create(productApiClient.getProductDetail("2"))
                .assertNext(product -> {
                    assertEquals("2", product.id());
                    assertEquals("Shirt", product.name());
                    assertEquals(35.99, product.price());
                    assertTrue(product.availability());
                })
                .verifyComplete();
    }
}