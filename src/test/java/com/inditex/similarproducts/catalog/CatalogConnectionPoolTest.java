package com.inditex.similarproducts.catalog;

import java.net.URI;
import java.time.Duration;
import java.util.concurrent.atomic.AtomicInteger;

import org.junit.jupiter.api.Test;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import reactor.netty.http.server.HttpServer;

import static org.assertj.core.api.Assertions.assertThat;

class CatalogConnectionPoolTest {
    @Test
    void replacesIdleConnectionsBeforeTheCatalogClosesThem() throws InterruptedException {
        AtomicInteger connections = new AtomicInteger();
        var server = HttpServer.create().host("127.0.0.1").port(0)
                .idleTimeout(Duration.ofSeconds(5))
                .doOnConnection(connection -> connections.incrementAndGet())
                .handle((request, response) -> response.header("Content-Type", "application/json")
                        .sendString(Mono.just("[\"2\"]")))
                .bindNow();
        var properties = new CatalogProperties(URI.create("http://127.0.0.1:" + server.port()),
                Duration.ofSeconds(1), Duration.ofSeconds(2), Duration.ofMillis(100), 2, 10, 10);
        var configuration = new CatalogConfiguration();
        var pool = configuration.catalogConnectionProvider(properties);
        try {
            var client = new CatalogClient(configuration.catalogWebClient(WebClient.builder(), properties, pool),
                    properties);
            assertThat(client.similarIds("source").block()).containsExactly("2");
            assertThat(connections).hasValue(1);

            Thread.sleep(250);

            assertThat(client.similarIds("source").block()).containsExactly("2");
            assertThat(connections).hasValue(2);
        } finally {
            pool.disposeLater().block(Duration.ofSeconds(5));
            server.disposeNow();
        }
    }
}
