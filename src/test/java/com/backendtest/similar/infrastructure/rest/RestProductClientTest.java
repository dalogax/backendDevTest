package com.backendtest.similar.infrastructure.rest;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.get;
import static com.github.tomakehurst.wiremock.client.WireMock.urlEqualTo;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.backendtest.similar.config.ProductProperties;
import com.backendtest.similar.config.RestClientConfig;
import com.backendtest.similar.dto.ProductDetail;
import com.backendtest.similar.exception.ProductNotFoundException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.core.WireMockConfiguration;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.web.client.RestClient;

class RestProductClientTest {

    private WireMockServer wiremock;
    private RestProductClient client;

    @BeforeEach
    void setUp() {
        wiremock = new WireMockServer(WireMockConfiguration.wireMockConfig().dynamicPort());
        wiremock.start();
        ProductProperties props = new ProductProperties("http://localhost:" + wiremock.port(), 2000, 500, 50, 60, 1000);
        RestClient restClient = new RestClientConfig().productRestClient(props);
        client = new RestProductClient(restClient, new ObjectMapper());
    }

    @AfterEach
    void tearDown() {
        wiremock.stop();
    }

    @Test
    void similaridsNumbersAreNormalizedToStrings() {
        wiremock.stubFor(get(urlEqualTo("/product/1/similarids"))
                .willReturn(aResponse()
                        .withHeader("Content-Type", "application/json")
                        .withBody("[2,3,4]")));

        assertThat(client.getSimilarIds("1")).isEqualTo(List.of("2", "3", "4"));
    }

    @Test
    void normalizeIdsTrimsAndDiscardsEmpties() {
        assertThat(client.normalizeIds("[\" 1 \",\"\", 2, null]")).isEqualTo(List.of("1", "2"));
        assertThat(client.normalizeIds("not-json")).isEmpty();
        assertThat(client.normalizeIds("{\"a\":1}")).isEmpty();
    }

    @Test
    void similarids404ThrowsNotFound() {
        wiremock.stubFor(get(urlEqualTo("/product/999/similarids"))
                .willReturn(aResponse().withStatus(404)));

        assertThatThrownBy(() -> client.getSimilarIds("999")).isInstanceOf(ProductNotFoundException.class);
    }

    @Test
    void productDetailOk() {
        wiremock.stubFor(get(urlEqualTo("/product/2"))
                .willReturn(aResponse()
                        .withHeader("Content-Type", "application/json")
                        .withBody("{\"id\":\"2\",\"name\":\"Dress\",\"price\":19.99,\"availability\":true}")));

        Optional<ProductDetail> first = client.getProduct("2");

        assertThat(first).isPresent();
        assertThat(first.get().name()).isEqualTo("Dress");
    }

    @Test
    void product404And500AreEmpty() {
        wiremock.stubFor(get(urlEqualTo("/product/5")).willReturn(aResponse().withStatus(404)));
        wiremock.stubFor(get(urlEqualTo("/product/6")).willReturn(aResponse().withStatus(500)));

        assertThat(client.getProduct("5")).isEmpty();
        assertThat(client.getProduct("6")).isEmpty();
    }

    @Test
    void productInvalidBodyIsEmpty() {
        wiremock.stubFor(get(urlEqualTo("/product/7"))
                .willReturn(aResponse()
                        .withHeader("Content-Type", "application/json")
                        .withBody("{\"message\":\"weird\"}")));

        assertThat(client.getProduct("7")).isEmpty();
    }

    @Test
    void productSlowBeyondTimeoutIsEmpty() {
        wiremock.stubFor(get(urlEqualTo("/product/1000"))
                .willReturn(aResponse()
                        .withHeader("Content-Type", "application/json")
                        .withFixedDelay(5000)
                        .withBody("{\"id\":\"1000\",\"name\":\"Coat\",\"price\":89.99,\"availability\":true}")));

        long start = System.currentTimeMillis();
        assertThat(client.getProduct("1000")).isEmpty();
        assertThat(System.currentTimeMillis() - start).isLessThan(4000);
    }
}
