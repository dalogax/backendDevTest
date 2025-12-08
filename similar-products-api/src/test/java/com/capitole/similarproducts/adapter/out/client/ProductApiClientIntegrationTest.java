package com.capitole.similarproducts.adapter.out.client;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.get;
import static com.github.tomakehurst.wiremock.client.WireMock.getRequestedFor;
import static com.github.tomakehurst.wiremock.client.WireMock.serverError;
import static com.github.tomakehurst.wiremock.client.WireMock.urlEqualTo;
import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.options;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.capitole.similarproducts.domain.exception.ExternalServiceException;
import com.github.tomakehurst.wiremock.WireMockServer;
import io.github.resilience4j.circuitbreaker.CircuitBreaker;
import io.github.resilience4j.circuitbreaker.CircuitBreakerRegistry;
import java.util.List;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import reactor.test.StepVerifier;

@SpringBootTest(properties = {
    "resilience4j.retry.instances.productService.wait-duration=50ms",
    "resilience4j.retry.instances.productService.max-attempts=5",
    "resilience4j.circuitbreaker.instances.productService.sliding-window-size=5",
    "resilience4j.circuitbreaker.instances.productService.minimum-number-of-calls=5"
})

class ProductApiClientIntegrationTest {

    private static WireMockServer wireMockServer;

    @Autowired
    private ProductApiClient productApiClient;

    @Autowired
    private CircuitBreakerRegistry circuitBreakerRegistry;

    @BeforeAll
    static void startWireMock() {
        wireMockServer = new WireMockServer(options().dynamicPort());
        wireMockServer.start();
    }

    @AfterAll
    static void stopWireMock() {
        wireMockServer.stop();
    }

    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
        registry.add("external.product-service.base-url", wireMockServer::baseUrl);
    }

    @BeforeEach
    void reset() {
        wireMockServer.resetAll();
        circuitBreakerRegistry.circuitBreaker("productService").transitionToClosedState();
    }

    @Test
    void getSimilarProductIds_ShouldReturnIds_WhenServiceReturnsSuccess() {
        String productId = "success-1";
        wireMockServer.stubFor(get(urlEqualTo("/product/" + productId + "/similarids"))
                .willReturn(aResponse()
                        .withHeader("Content-Type", "application/json")
                        .withBody("[\"1\", \"2\", \"3\"]")
                        .withStatus(200)));

        StepVerifier.create(productApiClient.getSimilarProductIds(productId))
                .expectNextMatches(list -> list.containsAll(List.of("1", "2", "3")))
                .verifyComplete();
    }

    @Test
    void getSimilarProductIds_ShouldRetry_WhenServiceFails() {
        String productId = "retry-base";
        wireMockServer.stubFor(get(urlEqualTo("/product/" + productId + "/similarids"))
                .willReturn(serverError()) // 1st attempt: 500
                .inScenario("Retry Scenario")
                .whenScenarioStateIs(com.github.tomakehurst.wiremock.stubbing.Scenario.STARTED)
                .willSetStateTo("Attempt 2"));

        wireMockServer.stubFor(get(urlEqualTo("/product/" + productId + "/similarids"))
                .inScenario("Retry Scenario")
                .whenScenarioStateIs("Attempt 2")
                .willReturn(serverError()) // 2nd attempt: 500
                .willSetStateTo("Attempt 3"));

        wireMockServer.stubFor(get(urlEqualTo("/product/" + productId + "/similarids"))
                .inScenario("Retry Scenario")
                .whenScenarioStateIs("Attempt 3")
                .willReturn(serverError()) // 3rd attempt: 500
                .willSetStateTo("Attempt 4"));

        wireMockServer.stubFor(get(urlEqualTo("/product/" + productId + "/similarids"))
                .inScenario("Retry Scenario")
                .whenScenarioStateIs("Attempt 4")
                .willReturn(aResponse()
                        .withHeader("Content-Type", "application/json")
                        .withBody("[\"2\", \"3\", \"4\"]"))); // 4th attempt: 200

        StepVerifier.create(productApiClient.getSimilarProductIds(productId))
                .expectNextMatches(list -> list.containsAll(List.of("2", "3", "4")))
                .verifyComplete();

        wireMockServer.verify(4, getRequestedFor(urlEqualTo("/product/" + productId + "/similarids")));
    }

    @Test
    void getSimilarProductIds_ShouldOpenCircuitBreaker_WhenFailuresPersist() {
        // Given
        String productId = "999";
        wireMockServer.stubFor(get(urlEqualTo("/product/" + productId + "/similarids"))
                .willReturn(serverError())); // Always 500

        CircuitBreaker circuitBreaker = circuitBreakerRegistry.circuitBreaker("productService");

        // When - Execute enough calls to trigger CB (Sliding window size is 10, failure rate 50%)
        // We need to generate failures. The fallback handles the error, but CB records it.
        for (int i = 0; i < 15; i++) {
             StepVerifier.create(productApiClient.getSimilarProductIds(productId))
                .expectError(ExternalServiceException.class)
                .verify();
        }

        // Then verify CircuitBreaker is OPEN
        assertThat(circuitBreaker.getState()).isEqualTo(CircuitBreaker.State.OPEN);
        
        // Reset requests to verify no new calls are made
        wireMockServer.resetRequests();
        
        // Verify fail-fast behavior: Request should be short-circuited (no new call to WireMock)
        StepVerifier.create(productApiClient.getSimilarProductIds(productId))
                .expectError(ExternalServiceException.class)
                .verify();
        
        // Verify no additional call made to WireMock
        wireMockServer.verify(0, getRequestedFor(urlEqualTo("/product/" + productId + "/similarids")));
    }

    @org.junit.jupiter.params.ParameterizedTest
    @org.junit.jupiter.params.provider.ValueSource(ints = {500, 502, 503, 504})
    void getSimilarProductIds_ShouldRetry_WhenServerError(int status) {
        String productId = "retry-" + status;
        
        // Mock failing attempts
        wireMockServer.stubFor(get(urlEqualTo("/product/" + productId + "/similarids"))
                .willReturn(aResponse().withStatus(status))
                .inScenario("Retry " + status)
                .whenScenarioStateIs(com.github.tomakehurst.wiremock.stubbing.Scenario.STARTED)
                .willSetStateTo("Attempt 2"));

        wireMockServer.stubFor(get(urlEqualTo("/product/" + productId + "/similarids"))
                .inScenario("Retry " + status)
                .whenScenarioStateIs("Attempt 2")
                .willReturn(aResponse().withStatus(status))
                .willSetStateTo("Attempt 3"));

        // Mock successful attempt
        wireMockServer.stubFor(get(urlEqualTo("/product/" + productId + "/similarids"))
                .inScenario("Retry " + status)
                .whenScenarioStateIs("Attempt 3")
                .willReturn(aResponse()
                        .withHeader("Content-Type", "application/json")
                        .withBody("[\"100\", \"101\"]")));

        StepVerifier.create(productApiClient.getSimilarProductIds(productId))
                .expectNextMatches(list -> list.containsAll(List.of("100", "101")))
                .verifyComplete();

        // Verify it retried 3 times (initial + 2 retries)
        wireMockServer.verify(3, getRequestedFor(urlEqualTo("/product/" + productId + "/similarids")));
    }

    @Test
    void getSimilarProductIds_ShouldRetry_OnNetworkFault() {
        String productId = "network-fault";

        // Mock connection reset (IOException equivalent)
        wireMockServer.stubFor(get(urlEqualTo("/product/" + productId + "/similarids"))
                .willReturn(aResponse().withFault(com.github.tomakehurst.wiremock.http.Fault.CONNECTION_RESET_BY_PEER))
                .inScenario("Network Retry")
                .whenScenarioStateIs(com.github.tomakehurst.wiremock.stubbing.Scenario.STARTED)
                .willSetStateTo("Attempt 2"));

        // Mock success
        wireMockServer.stubFor(get(urlEqualTo("/product/" + productId + "/similarids"))
                .inScenario("Network Retry")
                .whenScenarioStateIs("Attempt 2")
                .willReturn(aResponse()
                        .withHeader("Content-Type", "application/json")
                        .withBody("[\"200\"]")));

        StepVerifier.create(productApiClient.getSimilarProductIds(productId))
                .expectNextMatches(list -> list.contains("200"))
                .verifyComplete();

        wireMockServer.verify(2, getRequestedFor(urlEqualTo("/product/" + productId + "/similarids")));
    }
}
