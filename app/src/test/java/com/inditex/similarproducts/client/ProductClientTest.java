package com.inditex.similarproducts.client;

import com.inditex.similarproducts.config.WebClientConfig;
import com.inditex.similarproducts.exception.ProductNotFoundException;
import com.inditex.similarproducts.model.ProductDetail;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import okhttp3.mockwebserver.RecordedRequest;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import reactor.test.StepVerifier;

import java.io.IOException;
import java.time.Duration;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Unit tests for the upstream HTTP boundary, against a real (stubbed) socket so the
 * timeout and non-2xx handling are exercised the same way they are in production.
 */
class ProductClientTest {

    /** The production default — generous enough that a cold JVM can't trip the non-timing tests. */
    private static final long TIMEOUT_MS = 2000;

    /** Used only by the timeout tests, to keep the suite fast. */
    private static final long SHORT_TIMEOUT_MS = 300;

    /** Comfortably above SHORT_TIMEOUT_MS, so a slow stub can only ever lose the race. */
    private static final long UPSTREAM_DELAY_SECONDS = 3;

    private MockWebServer upstream;
    private ProductClient productClient;

    @BeforeEach
    void startUpstream() throws IOException {
        upstream = new MockWebServer();
        upstream.start();
        productClient = clientWithTimeout(TIMEOUT_MS);
    }

    /** Built through the production config so the pool/connector wiring is the one under test. */
    private ProductClient clientWithTimeout(long timeoutMs) {
        String baseUrl = "http://" + upstream.getHostName() + ":" + upstream.getPort();
        return new ProductClient(
                new WebClientConfig().productWebClient(baseUrl, 1000, 50, 2000),
                timeoutMs,
                timeoutMs);
    }

    @AfterEach
    void stopUpstream() throws IOException {
        upstream.shutdown();
    }

    // --- getSimilarIds -------------------------------------------------------

    @Test
    void getSimilarIdsReturnsIdsInUpstreamOrder() throws InterruptedException {
        upstream.enqueue(json("[\"2\",\"3\",\"4\"]"));

        StepVerifier.create(productClient.getSimilarIds("1"))
                .expectNext(List.of("2", "3", "4"))
                .verifyComplete();

        RecordedRequest request = upstream.takeRequest();
        assertThat(request.getPath()).isEqualTo("/product/1/similarids");
    }

    @Test
    void getSimilarIdsCoercesNumericIdsToStrings() {
        // Simulado answers with JSON numbers, not strings — they must survive as usable ids.
        upstream.enqueue(json("[2,3,4]"));

        StepVerifier.create(productClient.getSimilarIds("1"))
                .expectNext(List.of("2", "3", "4"))
                .verifyComplete();
    }

    @Test
    void getSimilarIdsReturnsEmptyListWhenUpstreamHasNoSimilarProducts() {
        upstream.enqueue(json("[]"));

        StepVerifier.create(productClient.getSimilarIds("1"))
                .expectNext(List.of())
                .verifyComplete();
    }

    @Test
    void getSimilarIdsFailsWithProductNotFoundOn404() {
        upstream.enqueue(new MockResponse().setResponseCode(404));

        StepVerifier.create(productClient.getSimilarIds("999"))
                .expectErrorSatisfies(error -> assertThat(error)
                        .isInstanceOf(ProductNotFoundException.class)
                        .hasMessageContaining("999"))
                .verify();
    }

    @Test
    void getSimilarIdsFailsOnUpstreamServerError() {
        upstream.enqueue(new MockResponse().setResponseCode(500));

        StepVerifier.create(productClient.getSimilarIds("1"))
                .expectErrorSatisfies(error -> assertThat(error)
                        // A 500 is not "no similar products" — it must not be mistaken for a 404.
                        .isNotInstanceOf(ProductNotFoundException.class)
                        .hasMessageContaining("Upstream error"))
                .verify();
    }

    @Test
    void getSimilarIdsTimesOutInsteadOfHangingOnAStalledUpstream() {
        upstream.enqueue(json("[\"2\"]").setHeadersDelay(UPSTREAM_DELAY_SECONDS, TimeUnit.SECONDS));

        StepVerifier.create(clientWithTimeout(SHORT_TIMEOUT_MS).getSimilarIds("1"))
                .expectError(TimeoutException.class)
                // Bound well below the upstream delay: the timeout must be what ends the call.
                .verify(Duration.ofSeconds(2));
    }

    // --- getProductDetail ----------------------------------------------------

    @Test
    void getProductDetailMapsEveryField() throws InterruptedException {
        upstream.enqueue(json("{\"id\":\"2\",\"name\":\"Dress\",\"price\":19.99,\"availability\":true}"));

        StepVerifier.create(productClient.getProductDetail("2"))
                .expectNext(new ProductDetail("2", "Dress", 19.99, true))
                .verifyComplete();

        RecordedRequest request = upstream.takeRequest();
        assertThat(request.getPath()).isEqualTo("/product/2");
    }

    @Test
    void getProductDetailSkipsProductOn404() {
        upstream.enqueue(new MockResponse().setResponseCode(404));

        // Skipped, not failed: one missing product must not sink the whole response.
        StepVerifier.create(productClient.getProductDetail("5"))
                .verifyComplete();
    }

    @Test
    void getProductDetailSkipsProductOnServerError() {
        upstream.enqueue(new MockResponse().setResponseCode(500));

        StepVerifier.create(productClient.getProductDetail("6"))
                .verifyComplete();
    }

    @Test
    void getProductDetailSkipsProductThatExceedsTheTimeout() {
        upstream.enqueue(json("{\"id\":\"1000\"}").setHeadersDelay(UPSTREAM_DELAY_SECONDS, TimeUnit.SECONDS));

        StepVerifier.create(clientWithTimeout(SHORT_TIMEOUT_MS).getProductDetail("1000"))
                .expectComplete()
                // Bound well below the upstream delay: the timeout must be what ends the call.
                .verify(Duration.ofSeconds(2));
    }

    @Test
    void getProductDetailSkipsProductOnUnparseableBody() {
        upstream.enqueue(json("not json"));

        StepVerifier.create(productClient.getProductDetail("2"))
                .verifyComplete();
    }

    private static MockResponse json(String body) {
        return new MockResponse()
                .setHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .setBody(body);
    }
}
