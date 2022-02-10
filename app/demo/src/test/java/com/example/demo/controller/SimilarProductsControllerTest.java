package com.example.demo.controller;

import com.example.demo.model.Product;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.client.WireMock;
import com.github.tomakehurst.wiremock.stubbing.StubMapping;
import lombok.SneakyThrows;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;

import static com.example.demo.controller.TestData.*;
import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.urlPathMatching;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;

@SpringBootTest(webEnvironment = RANDOM_PORT)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class SimilarProductsControllerTest {

    @Autowired
    private TestRestTemplate testRestTemplate;

    @Autowired
    private ObjectMapper objectMapper;

    @LocalServerPort
    protected int port;

    private final WireMockServer wireMockServer = getWireMockServer();

    private WireMockServer getWireMockServer() {
        WireMockServer wireMockServer = new WireMockServer(3001);
        wireMockServer.start();
        return wireMockServer;
    }

    @BeforeEach
    void setUp(){
        wireMockServer.resetAll();
    }

    @Test
    public void shouldReturnStatus404_WhenSimilarProductIdsNotFound() {
        String productId = "1";

        // given
        wireMockServer.stubFor(WireMock.get(urlPathMatching(String.format("/product/%s/similarids", productId)))
                .willReturn(aResponse()
                        .withStatus(HttpStatus.NOT_FOUND.value())
                        .withHeader("Content-type", MediaType.APPLICATION_JSON_VALUE)
                )
        );

        // when
        final ResponseEntity<String> response = this.testRestTemplate.getForEntity(
                String.format("http://localhost:%s/product/%s/similar", port, productId),
                String.class);


        // then
        Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        Assertions.assertThat(response.getBody()).isNotNull();
        Assertions.assertThat(response.getBody()).isEqualTo("Product Not found");

    }

    @Test
    public void shouldReturnStatus404_WhenSimilarProductNotFound() {
        String productId = "1";

        // given
        stubInWireMockServer(String.format("/product/%s/similarids", productId), asJson(givenSimilarProductIds));

        // when
        final ResponseEntity<String> response = this.testRestTemplate.getForEntity(
                String.format("http://localhost:%s/product/%s/similar", port, productId),
                String.class);


        // then
        Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        Assertions.assertThat(response.getBody()).isNotNull();
        Assertions.assertThat(response.getBody()).isEqualTo("Product Not found");

    }

    @Test
    public void shouldGetSimilarProducts() {
        String productId = "1";

        // given
        stubInWireMockServer(String.format("/product/%s/similarids", productId), asJson(givenSimilarProductIds));
        givenSimilarProducts.forEach(p -> stubInWireMockServer(String.format("/product/%s", p.getId()), asJson(p)));


        // when
        final ResponseEntity<Product[]> response = this.testRestTemplate.getForEntity(
                String.format("http://localhost:%s/product/%s/similar", port, productId),
                Product[].class);


        // then
        Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        Assertions.assertThat(response.getBody()).isNotNull();
        Assertions.assertThat(response.getBody().length).isEqualTo(givenSimilarProducts.size());
        Assertions.assertThat(response.getBody()).allMatch(p -> givenSimilarProductMap.get(p.getId()).equals(p));

    }

    private StubMapping stubInWireMockServer(String url, String body) {
        return wireMockServer.stubFor(WireMock.get(urlPathMatching(url))
                .willReturn(aResponse()
                        .withStatus(HttpStatus.OK.value())
                        .withHeader("Content-type", MediaType.APPLICATION_JSON_VALUE)
                        .withBody(body)
                )
        );
    }

    @SneakyThrows
    private String asJson(Object o) {
        return objectMapper.writeValueAsString(o);
    }

}
