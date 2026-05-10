package com.sngular.similarproducts.infrastructure.outbound;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withResourceNotFound;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;

import java.util.Collection;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.restclient.test.autoconfigure.AutoConfigureMockRestServiceServer;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.client.ExpectedCount;
import org.springframework.test.web.client.MockRestServiceServer;

import com.sngular.similarproducts.domain.ProductDetail;
import com.sngular.similarproducts.domain.exception.ProductNotFoundException;

import io.github.resilience4j.circuitbreaker.CircuitBreaker;
import io.github.resilience4j.circuitbreaker.CircuitBreakerRegistry;

@SpringBootTest(properties = {
        // Fast circuit breaker for testing
        "resilience4j.circuitbreaker.instances.productsService.sliding-window-size=4",
        "resilience4j.circuitbreaker.instances.productsService.minimum-number-of-calls=2",
        "resilience4j.circuitbreaker.instances.productsService.failure-rate-threshold=50",
        "resilience4j.circuitbreaker.instances.productsService.wait-duration-in-open-state=10s",
        "resilience4j.circuitbreaker.instances.productsService.permitted-number-of-calls-in-half-open-state=1",
        "resilience4j.circuitbreaker.instances.productsService.automatic-transition-from-open-to-half-open-enabled=false",
        // Disable retry to isolate circuit breaker behavior
        "resilience4j.retry.instances.productsService.max-attempts=1",
        "resilience4j.retry.instances.productsService.wait-duration=1ms"
})
@AutoConfigureMockRestServiceServer
class ProductsClientAdapterCircuitBreakerTest {

    @Autowired
    private ProductsClientAdapter adapter;

    @Autowired
    private CircuitBreakerRegistry circuitBreakerRegistry;

    @Autowired
    private MockRestServiceServer mockServer;

    @BeforeEach
    void setUp() {
        mockServer.reset();
        circuitBreakerRegistry.circuitBreaker("productsService").reset();
    }

    private static final String PRODUCT_SERVICE_BASE_URL = "http://localhost:3001/product";

    @Test
    void shouldReturnSimilarProductIdsSuccessfully() {
        mockServer.expect(requestTo(PRODUCT_SERVICE_BASE_URL + "/1/similarids"))
                .andRespond(withSuccess("[\"2\",\"3\"]", MediaType.APPLICATION_JSON));

        Collection<String> ids = adapter.getSimilarProductIds("1");

        assertEquals(2, ids.size());
        assertTrue(ids.contains("2"));
        assertTrue(ids.contains("3"));
        mockServer.verify();
    }

    @Test
    void shouldReturnProductDetailSuccessfully() {
        mockServer.expect(requestTo(PRODUCT_SERVICE_BASE_URL + "/2"))
                .andRespond(withSuccess("""
                        {"id":"2","name":"BMW i3","price":50000.0,"availability":true}
                        """, MediaType.APPLICATION_JSON));

        ProductDetail product = adapter.getProduct("2");

        assertEquals("2", product.id());
        assertEquals("BMW i3", product.name());
        assertEquals(50000.0, product.price());
        assertTrue(product.availability());
        mockServer.verify();
    }

    @Test
    void shouldReturnEmptyListOn404ForSimilarProductIds() {
        // 404 triggers SimilarProductsNotFoundException, but fallback returns empty
        // list
        mockServer.expect(requestTo(PRODUCT_SERVICE_BASE_URL + "/99/similarids"))
                .andRespond(withResourceNotFound());

        Collection<String> result = adapter.getSimilarProductIds("99");
        assertTrue(result.isEmpty());
        mockServer.verify();
    }

    @Test
    void shouldThrowProductNotFoundExceptionOn404() {
        mockServer.expect(requestTo(PRODUCT_SERVICE_BASE_URL + "/99"))
                .andRespond(withResourceNotFound());

        assertThrows(ProductNotFoundException.class,
                () -> adapter.getProduct("99"));
        mockServer.verify();
    }

    @Test
    void shouldOpenCircuitBreakerAfterRepeatedFailures() {
        CircuitBreaker cb = circuitBreakerRegistry.circuitBreaker("productsService");
        assertEquals(CircuitBreaker.State.CLOSED, cb.getState());

        // Simulate enough failures to open the circuit breaker
        // Config: sliding-window-size=4, minimum-number-of-calls=2,
        // failure-rate-threshold=50
        mockServer.expect(ExpectedCount.times(2), requestTo(PRODUCT_SERVICE_BASE_URL + "/99/similarids"))
                .andRespond(withResourceNotFound());

        // First failure - fallback returns empty list but CB records the failure
        Collection<String> result1 = adapter.getSimilarProductIds("99");
        assertTrue(result1.isEmpty());

        // Second failure - should trigger CB to OPEN
        Collection<String> result2 = adapter.getSimilarProductIds("99");
        assertTrue(result2.isEmpty());

        assertEquals(CircuitBreaker.State.OPEN, cb.getState());
        mockServer.verify();
    }

    @Test
    void shouldUseFallbackWhenCircuitBreakerIsOpen() {
        CircuitBreaker cb = circuitBreakerRegistry.circuitBreaker("productsService");

        // Force circuit breaker to OPEN state
        cb.transitionToOpenState();
        assertEquals(CircuitBreaker.State.OPEN, cb.getState());

        // When CB is open, fallback returns empty list for getSimilarProductIds
        Collection<String> result = adapter.getSimilarProductIds("1");
        assertTrue(result.isEmpty());
    }

    @Test
    void shouldUseFallbackForGetProductWhenCircuitBreakerIsOpen() {
        CircuitBreaker cb = circuitBreakerRegistry.circuitBreaker("productsService");

        // Force circuit breaker to OPEN state
        cb.transitionToOpenState();
        assertEquals(CircuitBreaker.State.OPEN, cb.getState());

        // When CB is open, fallback throws ProductNotFoundException
        assertThrows(ProductNotFoundException.class,
                () -> adapter.getProduct("1"));
    }

    @Test
    void circuitBreakerShouldRemainClosedOnSuccess() {
        CircuitBreaker cb = circuitBreakerRegistry.circuitBreaker("productsService");

        mockServer.expect(ExpectedCount.times(3), requestTo(PRODUCT_SERVICE_BASE_URL + "/1/similarids"))
                .andRespond(withSuccess("[\"2\"]", MediaType.APPLICATION_JSON));

        adapter.getSimilarProductIds("1");
        adapter.getSimilarProductIds("1");
        adapter.getSimilarProductIds("1");

        assertEquals(CircuitBreaker.State.CLOSED, cb.getState());
        mockServer.verify();
    }
}
