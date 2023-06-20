package com.pablolizarraga.solution.repository;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.pablolizarraga.solution.domain.ProductDetail;
import com.pablolizarraga.solution.infrastructure.adapter.out.repository.mock.SimilarProductRepositoryMock;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import org.junit.jupiter.api.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.client.WebClient;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

class SimilarProductRepositoryTest {

    private static ObjectMapper objectMapper;

    private static MockWebServer mockWebServer;

    private static WebClient webClient;

    private SimilarProductRepositoryMock similarProductRepositoryMock;

    @BeforeAll
    static void setUp() throws IOException {
        objectMapper = new ObjectMapper();

        mockWebServer = new MockWebServer();
        mockWebServer.start();

        WebClient webClient = WebClient.builder()
                .baseUrl(mockWebServer.url("/").toString())
                .build();
    }

    @BeforeEach
    void initialize() {
        similarProductRepositoryMock = new SimilarProductRepositoryMock(WebClient.builder()
                .baseUrl(String.format("http://localhost:%s", mockWebServer.getPort()))
                .build());
    }

    @AfterAll
    static void tearDown() throws IOException {
        mockWebServer.shutdown();
    }

    @Test
    void testGetSimilarProductIds() throws Exception {
        String productId = "1";
        List<String> expectedIds = List.of("2", "3");

        MockResponse mockResponse = new MockResponse()
                .setResponseCode(HttpStatus.OK.value())
                .setHeader("Content-Type", MediaType.APPLICATION_JSON_VALUE)
                .setBody(objectMapper.writeValueAsString(expectedIds));

        mockWebServer.enqueue(mockResponse);

        List<String> result = similarProductRepositoryMock.getSimilarProductIds(productId);
        Assertions.assertEquals(expectedIds, result);
    }

    @Test
    void testGetProduct() throws Exception {
        String productId = "1";
        ProductDetail expectedProduct = new ProductDetail("1", "Product 1", BigDecimal.TEN, true);

        MockResponse mockResponse = new MockResponse()
                .setResponseCode(HttpStatus.OK.value())
                .setHeader("Content-Type", MediaType.APPLICATION_JSON_VALUE)
                .setBody(objectMapper.writeValueAsString(expectedProduct));

        mockWebServer.enqueue(mockResponse);

        Optional<ProductDetail> result = similarProductRepositoryMock.getProduct(productId);

        assertThat(result)
                .isPresent()
                .map(ProductDetail::getId)
                .contains(expectedProduct.getId());
    }
}
