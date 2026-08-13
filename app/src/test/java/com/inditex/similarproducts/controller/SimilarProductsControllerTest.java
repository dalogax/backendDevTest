package com.inditex.similarproducts.controller;

import com.inditex.similarproducts.exception.ProductNotFoundException;
import com.inditex.similarproducts.model.ProductDetail;
import com.inditex.similarproducts.service.SimilarProductsService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Flux;

import static org.mockito.BDDMockito.given;

/**
 * Tests the HTTP contract of the endpoint in isolation from the upstream calls.
 */
@WebFluxTest(SimilarProductsController.class)
class SimilarProductsControllerTest {

    private static final ProductDetail PRODUCT_2 = new ProductDetail("2", "Dress", 19.99, true);
    private static final ProductDetail PRODUCT_3 = new ProductDetail("3", "Blazer", 29.99, false);

    @Autowired
    private WebTestClient webTestClient;

    @MockBean
    private SimilarProductsService similarProductsService;

    @Test
    void returnsTheSimilarProductsAsAJsonArray() {
        given(similarProductsService.getSimilarProducts("1"))
                .willReturn(Flux.just(PRODUCT_2, PRODUCT_3));

        webTestClient.get().uri("/product/1/similar")
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .expectBody()
                .jsonPath("$.length()").isEqualTo(2)
                .jsonPath("$[0].id").isEqualTo("2")
                .jsonPath("$[0].name").isEqualTo("Dress")
                .jsonPath("$[0].price").isEqualTo(19.99)
                .jsonPath("$[0].availability").isEqualTo(true)
                .jsonPath("$[1].id").isEqualTo("3")
                .jsonPath("$[1].availability").isEqualTo(false);
    }

    @Test
    void returnsAnEmptyArrayWhenNoSimilarProductCouldBeResolved() {
        // Every similar product timed out or errored — that is still a successful, empty answer.
        given(similarProductsService.getSimilarProducts("3")).willReturn(Flux.empty());

        webTestClient.get().uri("/product/3/similar")
                .exchange()
                .expectStatus().isOk()
                .expectBody().json("[]");
    }

    @Test
    void returns404WhenTheBaseProductDoesNotExist() {
        given(similarProductsService.getSimilarProducts("999"))
                .willReturn(Flux.error(new ProductNotFoundException("999")));

        webTestClient.get().uri("/product/999/similar")
                .exchange()
                .expectStatus().isNotFound()
                .expectBody().isEmpty();
    }

    @Test
    void returns500WhenTheUpstreamFailsUnexpectedly() {
        // Only a 404 from /similarids maps to 404; anything else is a genuine server-side failure.
        given(similarProductsService.getSimilarProducts("1"))
                .willReturn(Flux.error(new RuntimeException("Upstream error fetching similar IDs for product 1")));

        webTestClient.get().uri("/product/1/similar")
                .exchange()
                .expectStatus().is5xxServerError();
    }

    @Test
    void acceptsNonNumericProductIds() {
        given(similarProductsService.getSimilarProducts("abc")).willReturn(Flux.just(PRODUCT_2));

        webTestClient.get().uri("/product/abc/similar")
                .exchange()
                .expectStatus().isOk()
                .expectBody().jsonPath("$[0].id").isEqualTo("2");
    }
}
