package com.inditex.similarproducts;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Duration;
import java.util.List;
import java.util.Map;
import java.util.ArrayList;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

import com.sun.net.httpserver.HttpServer;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.junit.jupiter.params.provider.CsvSource;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.MediaType;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.reactive.server.WebTestClient;
import tools.jackson.databind.json.JsonMapper;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class SimilarProductsApiTest {
    private static final Map<String, Stub> STUBS = new ConcurrentHashMap<>();
    private static final Map<String, AtomicInteger> CALLS = new ConcurrentHashMap<>();
    private static final List<String> COMPLETED = new CopyOnWriteArrayList<>();
    private static final ExecutorService EXECUTOR = Executors.newVirtualThreadPerTaskExecutor();
    private static final HttpServer CATALOG = startCatalog();

    @LocalServerPort
    int port;
    private WebTestClient client;

    @DynamicPropertySource
    static void properties(DynamicPropertyRegistry registry) {
        registry.add("catalog.base-url", () -> "http://localhost:" + CATALOG.getAddress().getPort());
        registry.add("catalog.request-timeout", () -> "1500ms");
    }

    @BeforeEach
    void setUp() {
        STUBS.clear();
        CALLS.clear();
        COMPLETED.clear();
        client = WebTestClient.bindToServer()
                .baseUrl("http://localhost:" + port)
                .responseTimeout(Duration.ofSeconds(5))
                .build();
    }

    @AfterAll
    static void stopCatalog() {
        CATALOG.stop(0);
        EXECUTOR.shutdownNow();
    }

    @Test
    void returnsFullDetailsInSimilarityOrderEvenWhenResponsesFinishOutOfOrder() {
        stub("/product/source/similarids", 200, "[2,3,4]");
        STUBS.put("/product/2", new Stub(200, product("2", false), 200, null));
        stub("/product/3", 200, product("3", true));
        stub("/product/4", 200, product("4", true));

        similar("source").expectStatus().isOk()
                .expectHeader().contentTypeCompatibleWith(MediaType.APPLICATION_JSON)
                .expectBody()
                .json("[" + product("2", false) + "," + product("3", true) + "," + product("4", true) + "]")
                .jsonPath("$[0].id").isEqualTo("2")
                .jsonPath("$[1].id").isEqualTo("3")
                .jsonPath("$[2].id").isEqualTo("4");

        assertThat(COMPLETED.indexOf("/product/3")).isLessThan(COMPLETED.indexOf("/product/2"));
    }

    @Test
    void acceptsStringIdsAndDeduplicatesBeforeFetching() {
        stub("/product/source/similarids", 200, "[\"sku-B\",\"sku-A\",\"sku-B\"]");
        stub("/product/sku-B", 200, product("sku-B", true));
        stub("/product/sku-A", 200, product("sku-A", false));

        similar("source").expectStatus().isOk().expectBody()
                .jsonPath("$.length()").isEqualTo(2)
                .jsonPath("$[0].id").isEqualTo("sku-B")
                .jsonPath("$[1].id").isEqualTo("sku-A");

        assertThat(CALLS.get("/product/sku-B").get()).isEqualTo(1);
        assertThat(CALLS).doesNotContainKey("/product/source");
    }

    @Test
    void fetchesDetailsConcurrently() {
        stub("/product/source/similarids", 200, "[\"first\",\"second\"]");
        CountDownLatch bothStarted = new CountDownLatch(2);
        AtomicBoolean overlapped = new AtomicBoolean(true);
        Runnable barrier = () -> {
            bothStarted.countDown();
            try {
                if (!bothStarted.await(1, TimeUnit.SECONDS)) {
                    overlapped.set(false);
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                overlapped.set(false);
            }
        };
        STUBS.put("/product/first", new Stub(200, product("first", true), 0, barrier));
        STUBS.put("/product/second", new Stub(200, product("second", true), 0, barrier));

        similar("source").expectStatus().isOk().expectBody().jsonPath("$.length()").isEqualTo(2);

        assertThat(overlapped).isTrue();
        assertThat(bothStarted.getCount()).isZero();
    }

    @Test
    void returnsAnEmptyArrayWhenNoSimilarProductsExist() {
        stub("/product/source/similarids", 200, "[]");

        similar("source").expectStatus().isOk().expectBody().json("[]");

        assertThat(CALLS).hasSize(1);
    }

    @ParameterizedTest
    @ValueSource(ints = {404, 500, 503})
    void omitsFailedDetailsAndKeepsOtherProducts(int status) {
        stub("/product/source/similarids", 200, "[1,2,3]");
        stub("/product/1", 200, product("1", true));
        stub("/product/2", status, "{}");
        stub("/product/3", 200, product("3", true));

        similar("source").expectStatus().isOk().expectBody()
                .jsonPath("$.length()").isEqualTo(2)
                .jsonPath("$[0].id").isEqualTo("1")
                .jsonPath("$[1].id").isEqualTo("3");

        assertThat(CALLS.get("/product/2").get()).isEqualTo(1);
    }

    @Test
    void omitsTimedOutDetailsAndReturnsAvailableProducts() {
        stub("/product/source/similarids", 200, "[1,2]");
        STUBS.put("/product/1", new Stub(200, product("1", true), 3000, null));
        stub("/product/2", 200, product("2", true));

        similar("source").expectStatus().isOk().expectBody().json("[" + product("2", true) + "]");
    }

    @ParameterizedTest
    @ValueSource(strings = {"{invalid", "{}", "{\"id\":\"1\",\"name\":\"Item\",\"price\":9.99}", ""})
    void omitsMalformedOrEmptyDetails(String body) {
        stub("/product/source/similarids", 200, "[1,2]");
        stub("/product/1", 200, body);
        stub("/product/2", 200, product("2", true));

        similar("source").expectStatus().isOk().expectBody().json("[" + product("2", true) + "]");
    }

    @Test
    void returnsEmptyArrayWhenAllDetailsFail() {
        stub("/product/source/similarids", 200, "[1,2]");
        stub("/product/1", 404, "{}");
        stub("/product/2", 500, "{}");

        similar("source").expectStatus().isOk().expectBody().json("[]");
    }

    @Test
    void deduplicatesReturnedProductIds() {
        stub("/product/source/similarids", 200, "[1,2]");
        stub("/product/1", 200, product("1", true));
        stub("/product/2", 200, product("1", true));

        similar("source").expectStatus().isOk().expectBody().jsonPath("$.length()").isEqualTo(1);
    }

    @Test
    void returns404WhenTheSourceProductIsUnknown() {
        similar("unknown").expectStatus().isNotFound()
                .expectHeader().contentTypeCompatibleWith(MediaType.APPLICATION_PROBLEM_JSON)
                .expectBody().jsonPath("$.status").isEqualTo(404);
    }

    @Test
    void returns502WhenTheIdsServiceFails() {
        stub("/product/source/similarids", 500, "{}");

        similar("source").expectStatus().isEqualTo(502)
                .expectBody().jsonPath("$.status").isEqualTo(502);
    }

    @ParameterizedTest
    @ValueSource(strings = {"{broken", "{}", "", "[null]", "[\"\"]", "[\"1\",null]"})
    void returns502ForInvalidIdsWithoutFetchingDetails(String body) {
        stub("/product/source/similarids", 200, body);

        similar("source").expectStatus().isEqualTo(502);

        assertThat(CALLS).hasSize(1);
    }

    @Test
    void returns504WhenTheIdsServiceTimesOut() {
        STUBS.put("/product/source/similarids", new Stub(200, "[]", 3000, null));

        similar("source").expectStatus().isEqualTo(504)
                .expectBody().jsonPath("$.status").isEqualTo(504);
    }

    @ParameterizedTest
    @CsvSource({"1, '2,3,4'", "2, '3,100,1000'", "3, '100,1000'", "4, '1,2'", "5, '1,2'"})
    void implementsTheSuppliedScenarios(String source, String expectedIds) throws IOException {
        // Reuse the evaluator's data verbatim; shorten delays so this stays a fast integration test.
        var mapper = JsonMapper.builder().build();
        var mocks = mapper.readTree(Files.readString(Path.of("shared/simulado/mocks.json")));
        for (var mock : mocks) {
            STUBS.put(mock.path("path").asString(), new Stub(
                    mock.path("status").asInt(200), mock.path("body").asString(""),
                    mock.path("delay").asLong(0) / 10, null));
        }
        var products = similar(source).expectStatus().isOk().expectBody().returnResult().getResponseBody();
        var response = mapper.readTree(products);
        var actualIds = new ArrayList<String>();
        for (var product : response) {
            String id = product.path("id").asString();
            actualIds.add(id);
            assertThat(product).isEqualTo(mapper.readTree(STUBS.get("/product/" + id).body()));
        }
        assertThat(actualIds).containsExactly(expectedIds.split(","));
    }

    @Test
    void preservesDecimalPricesWithoutFloatingPointRounding() {
        stub("/product/source/similarids", 200, "[1]");
        stub("/product/1", 200,
                "{\"id\":\"1\",\"name\":\"Item\",\"price\":9007199254740993.123456789,\"availability\":false}");

        similar("source").expectStatus().isOk().expectBody(String.class)
                .value(body -> assertThat(body).contains("9007199254740993.123456789"));
    }

    @Test
    void returns502WhenTheIdsConnectionClosesWithoutAResponse() {
        stub("/product/source/similarids", 0, "");

        similar("source").expectStatus().isEqualTo(502);

        assertThat(CALLS.get("/product/source/similarids").get()).isEqualTo(1);
    }

    @Test
    void returns502WhenTheIdsResponseExceedsTheBufferLimit() {
        stub("/product/source/similarids", 200, "[\"" + "x".repeat(300_000) + "\"]");

        similar("source").expectStatus().isEqualTo(502);
    }

    @Test
    void omitsDisconnectedDetailsWithoutRetrying() {
        stub("/product/source/similarids", 200, "[1,2]");
        stub("/product/1", 0, "");
        stub("/product/2", 200, product("2", true));

        similar("source").expectStatus().isOk().expectBody().json("[" + product("2", true) + "]");

        assertThat(CALLS.get("/product/1").get()).isEqualTo(1);
    }

    @Test
    void servesTwoHundredConcurrentRequestsWithCompleteOrderedResults() throws InterruptedException {
        stub("/product/source/similarids", 200, "[1,2]");
        STUBS.put("/product/1", new Stub(200, product("1", true), 1000, null));
        stub("/product/2", 200, product("2", false));
        var request = HttpRequest.newBuilder(URI.create("http://localhost:" + port + "/product/source/similar"))
                .timeout(Duration.ofSeconds(10)).build();

        try (var http = HttpClient.newHttpClient()) {
            var requests = new ArrayList<CompletableFuture<HttpResponse<String>>>();
            for (int i = 0; i < 200; i++) {
                requests.add(http.sendAsync(request, HttpResponse.BodyHandlers.ofString()));
                // Ramp TCP establishment; the delayed detail keeps all application requests in flight.
                Thread.sleep(2);
            }
            assertThat(requests.stream().filter(result -> !result.isDone()).count()).isEqualTo(200);
            CompletableFuture.allOf(requests.toArray(CompletableFuture[]::new)).join();

            for (var result : requests) {
                assertThat(result.join().statusCode()).isEqualTo(200);
                assertThat(result.join().body()).isEqualTo("[" + product("1", true) + "," + product("2", false) + "]");
            }
        }
    }

    private WebTestClient.ResponseSpec similar(String id) {
        return client.get().uri("/product/{id}/similar", id).exchange();
    }

    private static String product(String id, boolean available) {
        return "{\"id\":\"" + id + "\",\"name\":\"Item " + id
                + "\",\"price\":19.99,\"availability\":" + available + "}";
    }

    private static void stub(String path, int status, String body) {
        STUBS.put(path, new Stub(status, body, 0, null));
    }

    private static HttpServer startCatalog() {
        try {
            HttpServer server = HttpServer.create(new InetSocketAddress("localhost", 0), 0);
            server.setExecutor(EXECUTOR);
            server.createContext("/", exchange -> {
                String path = exchange.getRequestURI().getPath();
                CALLS.computeIfAbsent(path, ignored -> new AtomicInteger()).incrementAndGet();
                Stub response = STUBS.getOrDefault(path, new Stub(404, "{}", 0, null));
                try (exchange) {
                    if (response.status() == 0) {
                        return;
                    }
                    if (response.beforeResponse() != null) {
                        response.beforeResponse().run();
                    }
                    Thread.sleep(response.delayMillis());
                    byte[] bytes = response.body().getBytes(StandardCharsets.UTF_8);
                    exchange.getResponseHeaders().set("Content-Type", "application/json");
                    exchange.sendResponseHeaders(response.status(), bytes.length == 0 ? -1 : bytes.length);
                    COMPLETED.add(path);
                    exchange.getResponseBody().write(bytes);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                } catch (IOException ignored) {
                    // A timed-out client has cancelled its request and closed the connection.
                }
            });
            server.start();
            return server;
        } catch (IOException e) {
            throw new ExceptionInInitializerError(e);
        }
    }

    private record Stub(int status, String body, long delayMillis, Runnable beforeResponse) { }
}
