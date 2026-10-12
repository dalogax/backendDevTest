package com.backendtest.similar.service;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.get;
import static com.github.tomakehurst.wiremock.client.WireMock.urlEqualTo;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.backendtest.similar.config.ProductProperties;
import com.backendtest.similar.config.RestClientConfig;
import com.backendtest.similar.dto.ProductDetail;
import com.backendtest.similar.exception.ProductNotFoundException;
import com.backendtest.similar.infrastructure.rest.RestProductClient;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.core.WireMockConfiguration;
import java.util.List;
import java.util.concurrent.Executors;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.web.client.RestClient;

/**
 * Flujo completo sin Docker: WireMock reproduce shared/simulado/mocks.json y el
 * servicio real (cliente + fan-out) se verifica de extremo a extremo.
 * Equivalente local del paso 4 cuando el daemon de Docker no está disponible.
 */
class SimilarFlowTest {

    private WireMockServer wiremock;
    private SimilarService service;

    @BeforeEach
    void setUp() {
        wiremock = new WireMockServer(WireMockConfiguration.wireMockConfig().dynamicPort());
        wiremock.start();
        stubMocks();
        ProductProperties props = new ProductProperties("http://localhost:" + wiremock.port(), 2000, 500, 50, 60, 1000);
        RestClient restClient = new RestClientConfig().productRestClient(props);
        RestProductClient client = new RestProductClient(restClient, new ObjectMapper());
        service = new SimilarService(client, props.timeoutMs(), Executors.newVirtualThreadPerTaskExecutor());
    }

    @AfterEach
    void tearDown() {
        wiremock.stop();
    }

    private void stub(String path, String body) {
        wiremock.stubFor(get(urlEqualTo(path))
                .willReturn(aResponse()
                        .withHeader("Content-Type", "application/json")
                        .withBody(body)));
    }

    private void stub(String path, String body, int delayMs) {
        wiremock.stubFor(get(urlEqualTo(path))
                .willReturn(aResponse()
                        .withHeader("Content-Type", "application/json")
                        .withFixedDelay(delayMs)
                        .withBody(body)));
    }

    private void stubMocks() {
        stub("/product/1/similarids", "[2,3,4]");
        stub("/product/2/similarids", "[3,100,1000]");
        stub("/product/3/similarids", "[100,1000,10000]");
        stub("/product/4/similarids", "[1,2,5]");
        stub("/product/5/similarids", "[1,2,6]");
        wiremock.stubFor(get(urlEqualTo("/product/999/similarids"))
                .willReturn(aResponse().withStatus(404)));

        stub("/product/1", "{\"id\":\"1\",\"name\":\"Shirt\",\"price\":9.99,\"availability\":true}");
        stub("/product/2", "{\"id\":\"2\",\"name\":\"Dress\",\"price\":19.99,\"availability\":true}");
        stub("/product/3", "{\"id\":\"3\",\"name\":\"Blazer\",\"price\":29.99,\"availability\":false}", 100);
        stub("/product/4", "{\"id\":\"4\",\"name\":\"Boots\",\"price\":39.99,\"availability\":true}");
        wiremock.stubFor(get(urlEqualTo("/product/5"))
                .willReturn(aResponse()
                        .withStatus(404)
                        .withHeader("Content-Type", "application/json")
                        .withBody("{\"message\":\"Product not found\"}")));
        wiremock.stubFor(get(urlEqualTo("/product/6"))
                .willReturn(aResponse().withStatus(500).withHeader("Content-Type", "application/json")));
        stub("/product/100", "{\"id\":\"100\",\"name\":\"Trousers\",\"price\":49.99,\"availability\":false}", 1000);
        stub("/product/1000", "{\"id\":\"1000\",\"name\":\"Coat\",\"price\":89.99,\"availability\":true}", 5000);
        // 10000 tarda 50s en mocks.json; aquí 10s basta para probar el filtrado por timeout de 2s
        // sin dejar hilos colgados 50s en el test.
        stub(
                "/product/10000",
                "{\"id\":\"10000\",\"name\":\"Leather jacket\",\"price\":89.99,\"availability\":true}",
                10000);
    }

    @Test
    void c1_directo_devuelveTresEnOrden() {
        assertThat(service.getSimilarProducts("1"))
                .extracting(ProductDetail::id)
                .containsExactly("2", "3", "4");
    }

    @Test
    void c4_detalle404_seFiltra() {
        assertThat(service.getSimilarProducts("4"))
                .extracting(ProductDetail::id)
                .containsExactly("1", "2");
    }

    @Test
    void c5_detalle500_seFiltra() {
        assertThat(service.getSimilarProducts("5"))
                .extracting(ProductDetail::id)
                .containsExactly("1", "2");
    }

    @Test
    void c6_coleccion404_propaga404() {
        assertThatThrownBy(() -> service.getSimilarProducts("999")).isInstanceOf(ProductNotFoundException.class);
    }

    @Test
    void c2_mixtoLento_filtraElDe5s() {
        long start = System.currentTimeMillis();
        List<ProductDetail> result = service.getSimilarProducts("2");
        long elapsed = System.currentTimeMillis() - start;

        assertThat(result).extracting(ProductDetail::id).containsExactly("3", "100");
        assertThat(elapsed).isLessThan(4000);
    }

    @Test
    void c3_muyLento_soloElDe1s() {
        long start = System.currentTimeMillis();
        List<ProductDetail> result = service.getSimilarProducts("3");
        long elapsed = System.currentTimeMillis() - start;

        assertThat(result).extracting(ProductDetail::id).containsExactly("100");
        assertThat(elapsed).isLessThan(4000);
    }
}
