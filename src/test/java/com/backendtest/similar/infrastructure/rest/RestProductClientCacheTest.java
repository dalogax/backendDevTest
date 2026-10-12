package com.backendtest.similar.infrastructure.rest;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.get;
import static com.github.tomakehurst.wiremock.client.WireMock.getRequestedFor;
import static com.github.tomakehurst.wiremock.client.WireMock.urlEqualTo;
import static org.assertj.core.api.Assertions.assertThat;

import com.backendtest.similar.port.ProductCatalogPort;
import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.core.WireMockConfiguration;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;

@SpringBootTest
class RestProductClientCacheTest {

    private static final WireMockServer wiremock =
            new WireMockServer(WireMockConfiguration.wireMockConfig().dynamicPort());

    @DynamicPropertySource
    static void upstream(DynamicPropertyRegistry registry) {
        registry.add("app.product.base-url", () -> "http://localhost:" + wiremock.port());
    }

    @BeforeAll
    static void startStubs() {
        wiremock.start();
        wiremock.stubFor(get(urlEqualTo("/product/2"))
                .willReturn(aResponse()
                        .withHeader("Content-Type", "application/json")
                        .withBody("{\"id\":\"2\",\"name\":\"Dress\",\"price\":19.99,\"availability\":true}")));
        wiremock.stubFor(get(urlEqualTo("/product/5")).willReturn(aResponse().withStatus(404)));
    }

    @AfterAll
    static void stop() {
        wiremock.stop();
    }

    @Autowired
    ProductCatalogPort port;

    @Test
    void secondCallIsServedFromCache() {
        assertThat(port.getProduct("2")).isPresent();
        assertThat(port.getProduct("2")).isPresent();

        wiremock.verify(1, getRequestedFor(urlEqualTo("/product/2")));
    }

    @Test
    void emptyResultIsNotCached() {
        assertThat(port.getProduct("5")).isEmpty();
        assertThat(port.getProduct("5")).isEmpty();

        wiremock.verify(2, getRequestedFor(urlEqualTo("/product/5")));
    }
}
