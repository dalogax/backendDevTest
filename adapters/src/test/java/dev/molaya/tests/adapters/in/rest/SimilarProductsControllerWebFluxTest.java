package dev.molaya.tests.adapters.in.rest;

import dev.molaya.tests.adapters.AdapterTestApplication;
import dev.molaya.tests.adapters.Stubs;
import dev.molaya.tests.adapters.in.rest.gen.openapi.dto.ProductDetail;
import dev.molaya.tests.application.in.GetSimilarProductsUseCase;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mapstruct.factory.Mappers;
import org.mockito.stubbing.Answer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webflux.test.autoconfigure.WebFluxTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Flux;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.when;

@WebFluxTest(controllers = SimilarProductsController.class)
@Import(AdapterTestApplication.class)
class SimilarProductsControllerWebFluxTest {
    @Autowired
    private WebTestClient webTestClient;

    @MockitoBean
    private GetSimilarProductsUseCase useCase;

    @MockitoBean
    private RestSimilarProductsMapper mapper;

    static final RestSimilarProductsMapper REAL_MAPPER = Mappers.getMapper(RestSimilarProductsMapper.class);

    @BeforeEach
    void setup() {
        when(mapper.toRestProductDetail(any())).thenAnswer(callRealMapper());
    }

    @Nested
    class HappyPath {
        @ParameterizedTest
        @ValueSource(strings = {"A", "B"})
        void returnsSimilarProducts(final String productId) {
            final var products = Flux.just(Stubs.product(productId), Stubs.product("B"));
            when(useCase.getSimilarProducts(any())).thenReturn(products);
            when(mapper.toRestProductDetail(any())).thenAnswer(callRealMapper());
            final var response = webTestClient
                    .get()
                    .uri("/products/{id}/similar", productId)
                    .exchange()
                    .expectStatus()
                    .isOk()
                    .returnResult(ProductDetail.class);
            assertNotNull(response.getResponseBody());
        }
    }

    private static Answer<Object> callRealMapper() {
        return val -> REAL_MAPPER.toRestProductDetail(val.getArgument(0));
    }

    @Nested
    class Exceptions {
        @ParameterizedTest
        @ValueSource(strings = {"A", " "})
        void returnsEmptyWhenNoProducts(final String productId) {
            when(useCase.getSimilarProducts(any())).thenReturn(Flux.empty());
            final var response = webTestClient
                    .get()
                    .uri("/products/{id}/similar", productId)
                    .exchange()
                    .expectStatus()
                    .isOk()
                    .returnResult(ProductDetail.class);
            assertEquals(0L, response.getResponseBody().count().block());
        }
    }
}
