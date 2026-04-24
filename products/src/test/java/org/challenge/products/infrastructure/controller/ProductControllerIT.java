package org.challenge.products.infrastructure.controller;

import com.github.tomakehurst.wiremock.WireMockServer;
import org.challenge.products.infrastructure.config.TestRedisConfig;
import org.challenge.products.infrastructure.dto.ErrorResponseDto;
import org.challenge.products.infrastructure.dto.ProductResponseDto;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.cache.CacheManager;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;
import org.wiremock.spring.ConfigureWireMock;
import org.wiremock.spring.EnableWireMock;
import org.wiremock.spring.InjectWireMock;

import static com.github.tomakehurst.wiremock.client.WireMock.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
@Import(TestRedisConfig.class)
@EnableWireMock(@ConfigureWireMock(port = 8089))
class ProductControllerIT {

    @LocalServerPort
    private Integer port;

    @Autowired
    private TestRestTemplate testRestTemplate;

    @InjectWireMock
    private WireMockServer wireMock;

    @Autowired
    private CacheManager cacheManager;

    @BeforeEach
    void setUp() {
        wireMock.resetAll();
        cacheManager.getCacheNames()
                .forEach(name -> cacheManager.getCache(name).clear());
    }

    @Test
    @DisplayName("Should return similar products for a valid product id")
    void shouldReturnSimilarProducts() {
        wireMock.stubFor(get(urlEqualTo("/product/1/similarids"))
                .willReturn(aResponse()
                        .withHeader("Content-Type", "application/json")
                        .withBody("[2, 3]")));

        wireMock.stubFor(get(urlEqualTo("/product/2"))
                .willReturn(aResponse()
                        .withHeader("Content-Type", "application/json")
                        .withBody("""
                            {"id":"2","name":"Product 2","price":19.99,"availability":true}
                        """)));

        wireMock.stubFor(get(urlEqualTo("/product/3"))
                .willReturn(aResponse()
                        .withHeader("Content-Type", "application/json")
                        .withBody("""
                            {"id":"3","name":"Product 3","price":29.99,"availability":true}
                        """)));

        ResponseEntity<ProductResponseDto[]> response =
                testRestTemplate.getForEntity(buildUrl("product/1/similar"), ProductResponseDto[].class);

        Assertions.assertEquals(HttpStatus.OK, response.getStatusCode());
        Assertions.assertNotNull(response.getBody());
        Assertions.assertEquals(2, response.getBody().length);
    }

    @Test
    @DisplayName("Should return 404 when similar ids endpoint returns 404")
    void shouldReturn404WhenProductNotFound() {
        wireMock.stubFor(get(urlEqualTo("/product/4/similarids"))
                .willReturn(aResponse().withStatus(404)));

        ResponseEntity<ErrorResponseDto> response =
                testRestTemplate.getForEntity(buildUrl("product/4/similar"), ErrorResponseDto.class);

        Assertions.assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        Assertions.assertNotNull(response.getBody());
        Assertions.assertNotNull(response.getBody().message());
    }

    @Test
    @DisplayName("Should return 200 with empty list when downstream returns 5xx — circuit breaker fallback")
    void shouldReturnEmptyListWhenDownstreamErrors() {
        wireMock.stubFor(get(urlEqualTo("/product/5/similarids"))
                .willReturn(aResponse().withStatus(500)));

        ResponseEntity<ProductResponseDto[]> response =
                testRestTemplate.getForEntity(buildUrl("product/5/similar"), ProductResponseDto[].class);

        // circuit breaker fallback returns List.of() — contract says 200 with empty list is valid
        Assertions.assertEquals(HttpStatus.OK, response.getStatusCode());
        Assertions.assertNotNull(response.getBody());
        Assertions.assertEquals(0, response.getBody().length);
    }

    @Test
    @DisplayName("Should skip product and return partial list when a product detail returns 404")
    void shouldSkipProductWhenDetailNotFound() {
        wireMock.stubFor(get(urlEqualTo("/product/1/similarids"))
                .willReturn(aResponse()
                        .withHeader("Content-Type", "application/json")
                        .withBody("[2, 99]")));

        wireMock.stubFor(get(urlEqualTo("/product/2"))
                .willReturn(aResponse()
                        .withHeader("Content-Type", "application/json")
                        .withBody("""
                            {"id":"2","name":"Product 2","price":19.99,"availability":true}
                        """)));

        wireMock.stubFor(get(urlEqualTo("/product/99"))
                .willReturn(aResponse().withStatus(404)));

        ResponseEntity<ProductResponseDto[]> response =
                testRestTemplate.getForEntity(buildUrl("product/1/similar"), ProductResponseDto[].class);

        // product 99 was skipped, product 2 returned
        Assertions.assertEquals(HttpStatus.OK, response.getStatusCode());
        Assertions.assertNotNull(response.getBody());
        Assertions.assertEquals(1, response.getBody().length);
        Assertions.assertEquals("2", response.getBody()[0].productId());
    }

    @Test
    @DisplayName("Should return empty list when product detail throws Any other exception")
    void shouldReturnEmptyListWhenProductDetailServiceThrowsAnyException() {
        wireMock.stubFor(get(urlEqualTo("/product/1/similarids"))
                .willReturn(aResponse()
                        .withHeader("Content-Type", "application/json")
                        .withBody("[2, 3]")));

        wireMock.stubFor(get(urlEqualTo("/product/2"))
                .willReturn(aResponse().withStatus(500)));

        wireMock.stubFor(get(urlEqualTo("/product/3"))
                .willReturn(aResponse().withStatus(500)));

        ResponseEntity<ProductResponseDto[]> response =
                testRestTemplate.getForEntity(buildUrl("product/1/similar"), ProductResponseDto[].class);

        Assertions.assertEquals(HttpStatus.OK, response.getStatusCode());
        Assertions.assertNotNull(response.getBody());
        Assertions.assertEquals(0, response.getBody().length);
    }

    @Test
    @DisplayName("Should serve result from cache on second request")
    void shouldCacheSimilarProducts() {
        wireMock.stubFor(get(urlEqualTo("/product/1/similarids"))
                .willReturn(aResponse()
                        .withHeader("Content-Type", "application/json")
                        .withBody("[2]")));

        wireMock.stubFor(get(urlEqualTo("/product/2"))
                .willReturn(aResponse()
                        .withHeader("Content-Type", "application/json")
                        .withBody("""
                            {"id":"2","name":"Product 2","price":19.99,"availability":true}
                        """)));

        String url = buildUrl("product/1/similar");

        testRestTemplate.getForEntity(url, ProductResponseDto[].class);
        testRestTemplate.getForEntity(url, ProductResponseDto[].class);

        wireMock.verify(1, getRequestedFor(urlEqualTo("/product/1/similarids")));
        wireMock.verify(1, getRequestedFor(urlEqualTo("/product/2")));
    }

    private String buildUrl(String path) {
        return "http://localhost:%s/%s".formatted(port, path);
    }
}