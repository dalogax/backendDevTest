package com.inditex.similarproducts.client;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.read.ListAppender;
import com.inditex.similarproducts.config.WebClientConfig;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import reactor.test.StepVerifier;

import java.io.IOException;
import java.time.Duration;
import java.util.List;
import java.util.concurrent.TimeUnit;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Pins the log levels of the upstream boundary.
 *
 * <p>The service skips products by design — under load that path runs on nearly every request, so
 * logging it above DEBUG would bury the failures that matter. These tests exist so that rule cannot
 * be relaxed by accident.
 */
class ProductClientLoggingTest {

    private static final long TIMEOUT_MS = 2000;
    private static final long SHORT_TIMEOUT_MS = 300;
    private static final long UPSTREAM_DELAY_SECONDS = 3;

    private MockWebServer upstream;
    private ProductClient productClient;

    private ch.qos.logback.classic.Logger clientLogger;
    private ListAppender<ILoggingEvent> logEvents;
    private Level originalLevel;

    @BeforeEach
    void startUpstream() throws IOException {
        upstream = new MockWebServer();
        upstream.start();
        productClient = clientWithTimeout(TIMEOUT_MS);

        // Attached after the client is built, so its startup INFO line stays out of the assertions.
        clientLogger = (ch.qos.logback.classic.Logger) LoggerFactory.getLogger(ProductClient.class);
        originalLevel = clientLogger.getLevel();
        clientLogger.setLevel(Level.DEBUG);
        logEvents = new ListAppender<>();
        logEvents.start();
        clientLogger.addAppender(logEvents);
    }

    @AfterEach
    void stopUpstream() throws IOException {
        clientLogger.detachAppender(logEvents);
        clientLogger.setLevel(originalLevel);
        upstream.shutdown();
    }

    @Test
    void skippingAProductThatReturns404IsOnlyLoggedAtDebug() {
        upstream.enqueue(new MockResponse().setResponseCode(404));

        StepVerifier.create(productClient.getProductDetail("5")).verifyComplete();

        assertThat(messagesAtLevel(Level.DEBUG)).anyMatch(message -> message.contains("Skipping product 5"));
        assertThat(eventsAtOrAbove(Level.INFO))
                .as("a skipped product is designed behaviour and must not reach INFO or above")
                .isEmpty();
    }

    @Test
    void skippingAProductThatTimesOutIsOnlyLoggedAtDebug() {
        upstream.enqueue(json("{\"id\":\"1000\"}").setHeadersDelay(UPSTREAM_DELAY_SECONDS, TimeUnit.SECONDS));
        ProductClient impatientClient = clientWithTimeout(SHORT_TIMEOUT_MS);
        logEvents.list.clear();   // drop that client's startup line

        StepVerifier.create(impatientClient.getProductDetail("1000"))
                .expectComplete()
                .verify(Duration.ofSeconds(2));

        assertThat(messagesAtLevel(Level.DEBUG)).anyMatch(message -> message.contains("Skipping product 1000"));
        assertThat(eventsAtOrAbove(Level.INFO))
                .as("timeouts are the expected outcome for the slow products and must stay at DEBUG")
                .isEmpty();
    }

    @Test
    void aProductWithNoSimilarIdsIsNotLoggedAsAFailure() {
        upstream.enqueue(new MockResponse().setResponseCode(404));

        StepVerifier.create(productClient.getSimilarIds("999")).verifyError();

        assertThat(eventsAtOrAbove(Level.WARN))
                .as("a 404 from /similarids is a 404 response to the client, not a fault")
                .isEmpty();
    }

    @Test
    void anUpstreamErrorOnTheEntryPointCallIsLoggedAtWarn() {
        upstream.enqueue(new MockResponse().setResponseCode(500));

        StepVerifier.create(productClient.getSimilarIds("1")).verifyError();

        // This one fails the whole request — it has to be visible without turning DEBUG on.
        assertThat(messagesAtLevel(Level.WARN))
                .anyMatch(message -> message.contains("fetching similar IDs for product 1"));
    }

    @Test
    void aTimeoutOnTheEntryPointCallIsLoggedAtWarn() {
        upstream.enqueue(json("[\"2\"]").setHeadersDelay(UPSTREAM_DELAY_SECONDS, TimeUnit.SECONDS));
        ProductClient impatientClient = clientWithTimeout(SHORT_TIMEOUT_MS);
        logEvents.list.clear();

        StepVerifier.create(impatientClient.getSimilarIds("1"))
                .expectError()
                .verify(Duration.ofSeconds(2));

        assertThat(messagesAtLevel(Level.WARN))
                .anyMatch(message -> message.contains("Timed out") && message.contains("product 1"));
    }

    private List<String> messagesAtLevel(Level level) {
        return logEvents.list.stream()
                .filter(event -> event.getLevel() == level)
                .map(ILoggingEvent::getFormattedMessage)
                .toList();
    }

    private List<String> eventsAtOrAbove(Level level) {
        return logEvents.list.stream()
                .filter(event -> event.getLevel().isGreaterOrEqual(level))
                .map(event -> event.getLevel() + " " + event.getFormattedMessage())
                .toList();
    }

    private ProductClient clientWithTimeout(long timeoutMs) {
        String baseUrl = "http://" + upstream.getHostName() + ":" + upstream.getPort();
        return new ProductClient(
                new WebClientConfig().productWebClient(baseUrl, 1000, 50, 2000),
                timeoutMs,
                timeoutMs);
    }

    private static MockResponse json(String body) {
        return new MockResponse()
                .setHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .setBody(body);
    }
}
