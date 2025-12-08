package com.capitole.similarproducts.acceptance;

import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.client.WireMock;
import com.github.tomakehurst.wiremock.core.WireMockConfiguration;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.test.context.DynamicPropertyRegistry;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;

/**
 * Acceptance tests for the Similar Products API.
 * These tests verify the API behavior from an end-user perspective.
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@DisplayName("Similar Products API Acceptance Tests")
class SimilarProductsAcceptanceTest {

    @LocalServerPort
    private int port;

    private static WireMockServer wireMockServer;

    @BeforeAll
    static void setupWireMock() {
        wireMockServer = new WireMockServer(WireMockConfiguration.options().dynamicPort());
        wireMockServer.start();
        
        // Configure WireMock stubs for product service
        setupProductServiceStubs();
        
        RestAssured.enableLoggingOfRequestAndResponseIfValidationFails();
    }

    @org.junit.jupiter.api.AfterAll
    static void tearDownWireMock() {
        if (wireMockServer != null && wireMockServer.isRunning()) {
            wireMockServer.stop();
        }
    }

    @org.springframework.test.context.DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
        registry.add("external.product-service.base-url", 
            () -> "http://localhost:" + wireMockServer.port());
    }

    @org.junit.jupiter.api.BeforeEach
    void setUp() {
        RestAssured.baseURI = "http://localhost";
        RestAssured.port = port;
    }

    private static void setupProductServiceStubs() {
        // Mock GET /product/1/similarids - returns similar product IDs
        wireMockServer.stubFor(WireMock.get(WireMock.urlEqualTo("/product/1/similarids"))
            .willReturn(WireMock.aResponse()
                .withStatus(200)
                .withHeader("Content-Type", "application/json")
                .withBody("[\"2\", \"3\", \"4\"]")));

        // Mock GET /product/999/similarids - returns empty array (no similar products)
        wireMockServer.stubFor(WireMock.get(WireMock.urlEqualTo("/product/999/similarids"))
            .willReturn(WireMock.aResponse()
                .withStatus(200)
                .withHeader("Content-Type", "application/json")
                .withBody("[]")));

        // Mock GET /product/2 - product details
        wireMockServer.stubFor(WireMock.get(WireMock.urlEqualTo("/product/2"))
            .willReturn(WireMock.aResponse()
                .withStatus(200)
                .withHeader("Content-Type", "application/json")
                .withBody("{\"id\":\"2\",\"name\":\"Product 2\",\"price\":29.99,\"availability\":true}")));

        // Mock GET /product/3 - product details
        wireMockServer.stubFor(WireMock.get(WireMock.urlEqualTo("/product/3"))
            .willReturn(WireMock.aResponse()
                .withStatus(200)
                .withHeader("Content-Type", "application/json")
                .withBody("{\"id\":\"3\",\"name\":\"Product 3\",\"price\":39.99,\"availability\":false}")));

        // Mock GET /product/4 - product details
        wireMockServer.stubFor(WireMock.get(WireMock.urlEqualTo("/product/4"))
            .willReturn(WireMock.aResponse()
                .withStatus(200)
                .withHeader("Content-Type", "application/json")
                .withBody("{\"id\":\"4\",\"name\":\"Product 4\",\"price\":49.99,\"availability\":true}")));
    }

    @Test
    @DisplayName("GET /product/{id}/similar should return 200 with array of products")
    void getSimilarProducts_ShouldReturn200WithProducts() {
        given()
            .pathParam("productId", "1")
        .when()
            .get("/product/{productId}/similar")
        .then()
            .statusCode(200)
            .contentType(ContentType.JSON)
            .body("$", isA(java.util.List.class))
            .body("size()", greaterThanOrEqualTo(0));
    }

    @Test
    @DisplayName("GET /product/{id}/similar should return products with correct structure")
    void getSimilarProducts_ShouldReturnCorrectStructure() {
        given()
            .pathParam("productId", "1")
        .when()
            .get("/product/{productId}/similar")
        .then()
            .statusCode(200)
            .contentType(ContentType.JSON)
            .body("$", everyItem(hasKey("id")))
            .body("$", everyItem(hasKey("name")))
            .body("$", everyItem(hasKey("price")))
            .body("$", everyItem(hasKey("availability")));
    }

    @Test
    @DisplayName("GET /product/{id}/similar should return empty array for product with no similar items")
    void getSimilarProducts_ShouldReturnEmptyArrayWhenNoSimilarProducts() {
        given()
            .pathParam("productId", "999")
        .when()
            .get("/product/{productId}/similar")
        .then()
            .statusCode(200)
            .contentType(ContentType.JSON)
            .body("$", hasSize(0));
    }

    @Test
    @DisplayName("GET /product/{id}/similar should validate product IDs are strings")
    void getSimilarProducts_ShouldReturnProductsWithStringIds() {
        given()
            .pathParam("productId", "1")
        .when()
            .get("/product/{productId}/similar")
        .then()
            .statusCode(200)
            .body("id", everyItem(isA(String.class)));
    }

    @Test
    @DisplayName("GET /product/{id}/similar should validate prices are numbers")
    void getSimilarProducts_ShouldReturnProductsWithNumericPrices() {
        given()
            .pathParam("productId", "1")
        .when()
            .get("/product/{productId}/similar")
        .then()
            .statusCode(200)
            .body("price", everyItem(isA(Number.class)));
    }

    @Test
    @DisplayName("GET /product/{id}/similar should validate availability is boolean")
    void getSimilarProducts_ShouldReturnProductsWithBooleanAvailability() {
        given()
            .pathParam("productId", "1")
        .when()
            .get("/product/{productId}/similar")
        .then()
            .statusCode(200)
            .body("availability", everyItem(isA(Boolean.class)));
    }

    @Test
    @DisplayName("GET /product/{id}/similar should return correct Content-Type header")
    void getSimilarProducts_ShouldReturnCorrectContentType() {
        given()
            .pathParam("productId", "1")
        .when()
            .get("/product/{productId}/similar")
        .then()
            .statusCode(200)
            .header("Content-Type", containsString("application/json"));
    }
}
