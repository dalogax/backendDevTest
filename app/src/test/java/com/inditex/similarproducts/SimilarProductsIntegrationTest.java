package com.inditex.similarproducts;

import okhttp3.mockwebserver.Dispatcher;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import okhttp3.mockwebserver.RecordedRequest;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.reactive.server.WebTestClient;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * End-to-end test of the whole chain — controller, service, client, WebClient and a real
 * socket — against a stub that reproduces the Simulado scenarios the k6 test drives.
 *
 * <p>Delays are scaled down (5s upstream vs a 1.5s timeout) so the suite stays fast while
 * keeping the same shape: some products cannot possibly arrive in time. The timeout stays
 * generous enough that a cold context can never make a healthy product look slow.
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class SimilarProductsIntegrationTest {

    private static final long DETAIL_TIMEOUT_MS = 1500;
    private static final long UPSTREAM_DELAY_SECONDS = 5;

    /** productId -> similar ids, mirroring the Simulado mock. */
    private static final Map<String, String> SIMILAR_IDS = Map.of(
            "1", "[\"2\",\"3\",\"4\"]",
            "2", "[\"3\",\"100\",\"1000\"]",
            "3", "[\"100\",\"1000\",\"10000\"]",
            "4", "[\"1\",\"2\",\"5\"]",
            "5", "[\"1\",\"2\",\"6\"]");

    /** Products that never answer in time — as products 1000 (5s) and 10000 (50s) do upstream. */
    private static final List<String> TOO_SLOW = List.of("1000", "10000");

    private static final MockWebServer UPSTREAM = new MockWebServer();

    static {
        try {
            UPSTREAM.start();
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
        UPSTREAM.setDispatcher(new SimuladoDispatcher());
    }

    @Autowired
    private WebTestClient webTestClient;

    @DynamicPropertySource
    static void upstreamProperties(DynamicPropertyRegistry registry) {
        registry.add("product-api.base-url",
                () -> "http://" + UPSTREAM.getHostName() + ":" + UPSTREAM.getPort());
        registry.add("product-api.detail-timeout-ms", () -> DETAIL_TIMEOUT_MS);
        registry.add("product-api.similar-ids-timeout-ms", () -> DETAIL_TIMEOUT_MS);
    }

    @AfterAll
    static void stopUpstream() throws IOException {
        UPSTREAM.shutdown();
    }

    @Test
    void returnsEverySimilarProductWhenAllUpstreamsAnswer() {
        webTestClient.get().uri("/product/1/similar")
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.length()").isEqualTo(3)
                .jsonPath("$[0].id").isEqualTo("2")
                .jsonPath("$[1].id").isEqualTo("3")
                .jsonPath("$[2].id").isEqualTo("4")
                .jsonPath("$[0].name").isEqualTo("Product 2")
                .jsonPath("$[0].price").isEqualTo(19.99)
                .jsonPath("$[0].availability").isEqualTo(true);
    }

    @Test
    void skipsTheSlowProductAndAnswersWithinTheTimeout() {
        long startedAt = System.nanoTime();

        webTestClient.get().uri("/product/2/similar")
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.length()").isEqualTo(2)
                .jsonPath("$[0].id").isEqualTo("3")
                .jsonPath("$[1].id").isEqualTo("100");

        long elapsedMs = (System.nanoTime() - startedAt) / 1_000_000;
        assertThat(elapsedMs)
                .as("must give up on the slow product, not wait it out")
                .isLessThan(TimeUnit.SECONDS.toMillis(UPSTREAM_DELAY_SECONDS) - 1000);
    }

    @Test
    void skipsEverySlowProductAndStillReturnsTheFastOne() {
        webTestClient.get().uri("/product/3/similar")
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.length()").isEqualTo(1)
                .jsonPath("$[0].id").isEqualTo("100");
    }

    @Test
    void skipsProductsThatReturn404() {
        webTestClient.get().uri("/product/4/similar")
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.length()").isEqualTo(2)
                .jsonPath("$[0].id").isEqualTo("1")
                .jsonPath("$[1].id").isEqualTo("2");
    }

    @Test
    void skipsProductsThatReturn500() {
        webTestClient.get().uri("/product/5/similar")
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.length()").isEqualTo(2)
                .jsonPath("$[0].id").isEqualTo("1")
                .jsonPath("$[1].id").isEqualTo("2");
    }

    @Test
    void returns404WhenTheProductHasNoSimilarIdsEndpoint() {
        webTestClient.get().uri("/product/999/similar")
                .exchange()
                .expectStatus().isNotFound();
    }

    /** Serves by path rather than by queue order, since the detail calls arrive concurrently. */
    private static class SimuladoDispatcher extends Dispatcher {

        @Override
        public MockResponse dispatch(RecordedRequest request) {
            String path = request.getPath() == null ? "" : request.getPath();

            if (path.endsWith("/similarids")) {
                String id = path.substring("/product/".length(), path.length() - "/similarids".length());
                String ids = SIMILAR_IDS.get(id);
                return ids == null ? new MockResponse().setResponseCode(404) : json(ids);
            }

            String id = path.substring(path.lastIndexOf('/') + 1);
            return switch (id) {
                case "5" -> new MockResponse().setResponseCode(404);
                case "6" -> new MockResponse().setResponseCode(500);
                default -> {
                    MockResponse response = json(product(id));
                    yield TOO_SLOW.contains(id)
                            ? response.setHeadersDelay(UPSTREAM_DELAY_SECONDS, TimeUnit.SECONDS)
                            : response;
                }
            };
        }

        private static String product(String id) {
            return "{\"id\":\"%s\",\"name\":\"Product %s\",\"price\":19.99,\"availability\":true}"
                    .formatted(id, id);
        }

        private static MockResponse json(String body) {
            return new MockResponse()
                    .setHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                    .setBody(body);
        }
    }
}
