package dev.molaya.tests.adapters.in.rest;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.when;

import dev.molaya.tests.adapters.Stubs;
import dev.molaya.tests.application.in.GetSimilarProductsUseCase;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Flux;

@ExtendWith(MockitoExtension.class)
class SimilarProductsControllerTest {
    @Mock
    private GetSimilarProductsUseCase useCase;

    @Mock
    private RestSimilarProductsMapper mapper;

    @Mock
    private ServerWebExchange exchange;

    @InjectMocks
    private SimilarProductsController controller;

    @Nested
    class HappyPath {
        @ParameterizedTest()
        @ValueSource(strings = {"A", "B", "C"})
        void returnsSimilarProducts(final String productId) {
            final var products = Flux.just(Stubs.product(productId), Stubs.product("B"));
            when(useCase.getSimilarProducts(any())).thenReturn(products);
            when(mapper.toGetSimilarProductsInput(any())).thenCallRealMethod();
            when(mapper.wrapAsOkResponse(any())).thenCallRealMethod();

            final var resultMono = controller.getProductSimilar(productId, exchange);
            final var response = resultMono.block();
            assertNotNull(response);
            assertEquals(HttpStatusCode.valueOf(200), response.getStatusCode());
            final var details = response.getBody();
            assertNotNull(details);
        }
    }

    @Nested
    class Exceptions {

        public static final String DEFAULT_PRODUCT_ID = "A";

        @Test
        void returnsEmptyWhenNoProducts() {
            when(useCase.getSimilarProducts(any())).thenReturn(Flux.empty());
            when(mapper.toGetSimilarProductsInput(any())).thenCallRealMethod();
            when(mapper.wrapAsOkResponse(any())).thenCallRealMethod();

            final var resultMono = controller.getProductSimilar(DEFAULT_PRODUCT_ID, exchange);
            final var response = resultMono.block();
            assertNotNull(response);
            assertEquals(ResponseEntity.ok(Flux.empty()).getStatusCode(), response.getStatusCode());
        }

        @Test
        void propagatesErrorFromUseCase() {
            when(useCase.getSimilarProducts(any())).thenReturn(Flux.error(new RuntimeException("fail")));
            when(mapper.toGetSimilarProductsInput(any())).thenCallRealMethod();
            when(mapper.wrapAsOkResponse(any())).thenCallRealMethod();

            final var resultMono = controller.getProductSimilar(DEFAULT_PRODUCT_ID, exchange);
            final var responseEntity = resultMono.block();
            assertNotNull(responseEntity);
            final var bodyFlux = responseEntity.getBody();
            assertNotNull(bodyFlux);
            assertThrows(RuntimeException.class, bodyFlux::blockFirst);
        }

        @Test
        void propagatesErrorFromUseCaseException() {
            when(useCase.getSimilarProducts(any())).thenThrow(new RuntimeException("fail"));

            assertThrows(RuntimeException.class, () -> controller.getProductSimilar(DEFAULT_PRODUCT_ID, exchange));
        }
    }
}
