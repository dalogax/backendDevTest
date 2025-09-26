package com.example.similarproducts.controller;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

import com.example.similarproducts.SimilarProductApplication;
import java.io.IOException;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.reactive.server.WebTestClient;

@SpringBootTest(
    classes = SimilarProductApplication.class,
    webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
    properties = {
        "clients.products.base-url=http://localhost:9999",
        "clients.products.connect-timeout-ms=500",
        "clients.products.read-timeout-ms=1000",
        "clients.products.product=/product/{id}",
        "clients.products.similar-ids=/product/{id}/similarids"
    }
)
class ProductControllerIntegrationTest {

  @Autowired
  private WebTestClient webTestClient;

  private static MockWebServer mockWebServer;

  @BeforeAll
  static void setUp() throws IOException {
    mockWebServer = new MockWebServer();
    mockWebServer.start(9999);
  }

  @AfterAll
  static void tearDown() throws IOException {
    mockWebServer.shutdown();
  }

  @Test
  void getSimilarProducts_returnsOkAndList() {
    mockWebServer.enqueue(new MockResponse()
        .setBody("[\"2\",\"3\"]")
        .addHeader("Content-Type", "application/json"));

    mockWebServer.enqueue(new MockResponse()
        .setBody("{\"id\":\"2\",\"name\":\"Prod2\",\"price\":20.0,\"availability\":true}")
        .addHeader("Content-Type", "application/json"));

    mockWebServer.enqueue(new MockResponse()
        .setBody("{\"id\":\"3\",\"name\":\"Prod3\",\"price\":30.0,\"availability\":false}")
        .addHeader("Content-Type", "application/json"));

    webTestClient.get()
        .uri("/product/1/similar")
        .exchange()
        .expectStatus().isOk()
        .expectBody()
        .jsonPath("$").isArray()
        .jsonPath("$.length()").value(len -> {
          assertThat((Integer) len).isGreaterThanOrEqualTo(1);
        })
        .jsonPath("$[0].id").isEqualTo("2");

  }

  @Test
  void getSimilarProducts_returns404WhenNotFound() {
    mockWebServer.enqueue(new MockResponse().setResponseCode(404));

    webTestClient.get()
        .uri("/product/999/similar")
        .exchange()
        .expectStatus().isNotFound()
        .expectBody()
        .jsonPath("title").isEqualTo("Product not found");
  }
}