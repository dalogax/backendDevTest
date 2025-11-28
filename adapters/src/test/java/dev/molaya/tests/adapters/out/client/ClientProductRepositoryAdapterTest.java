package dev.molaya.tests.adapters.out.client;

import dev.molaya.tests.adapters.Stubs;
import dev.molaya.tests.adapters.out.client.gen.openapi.DefaultGenApi;
import dev.molaya.tests.application.out.dto.ProductCommand;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Mono;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ClientProductRepositoryAdapterTest {
    @InjectMocks
    private ClientProductRepositoryAdapter adapter;

    @Mock
    private DefaultGenApi defaultApi;

    @Mock
    private ClientSimilarProductsMapper mapper;

    @Nested
    class HappyPath {
        @ParameterizedTest
        @ValueSource(strings = {"1", "2"})
        void getProductDetail(final String productId) {
            final var input = new ProductCommand(productId);
            when(defaultApi.getProductProductId(productId)).thenReturn(Mono.just(Stubs.productDetailOUT()));
            when(mapper.toDomainProduct(any())).thenReturn(Stubs.product());
            final var result = adapter.getProductDetail(input).block();
            assertNotNull(result);
            assertEquals(Stubs.product().id(), result.id());
        }

        @ParameterizedTest
        @ValueSource(strings = {"1", "2"})
        void getSimilarProductIds(final String productId) {
            final var input = new ProductCommand(productId);
            when(defaultApi.getProductSimilarids(productId)).thenReturn(Mono.just(Stubs.similarIds()));
            final var result = adapter.getSimilarProductIds(input).block();
            assertNotNull(result);
            assertTrue(result.contains("A"));
        }
    }

    @Nested
    class Exceptions {
        @Test
        void getProductDetailError_notThrownException() {
            final var input = new ProductCommand("productId");
            when(defaultApi.getProductProductId(any())).thenReturn(Mono.error(new RuntimeException("fail")));
            assertDoesNotThrow(() -> adapter.getProductDetail(input).block());
        }

        @Test
        void getProductDetailErrorOutOfFlow_doesNotThrowException() {
            final var input = new ProductCommand("productId");
            when(defaultApi.getProductProductId(any())).thenThrow(new RuntimeException("fail"));
            assertDoesNotThrow(() -> adapter.getProductDetail(input).block());
        }

        @Test
        void getProductDetailError_throwExceptionOnNullInput() {
            assertThrows(
                    RuntimeException.class, () -> adapter.getProductDetail(null).block());
        }
    }
}
