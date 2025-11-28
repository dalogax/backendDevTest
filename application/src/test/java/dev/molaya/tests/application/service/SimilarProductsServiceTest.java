package dev.molaya.tests.application.service;

import static dev.molaya.tests.application.service.Stubs.productIds;
import static dev.molaya.tests.application.service.Stubs.validCommand;
import static dev.molaya.tests.application.service.Stubs.validInput;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.when;

import dev.molaya.tests.application.config.SimilarProductsConfigurationProperties;
import dev.molaya.tests.application.in.dto.GetSimilarProductsInput;
import dev.molaya.tests.application.out.ProductRepositoryPort;
import dev.molaya.tests.application.out.dto.ProductCommand;
import dev.molaya.tests.domain.Product;
import dev.molaya.tests.domain.exceptions.IntegrationException;
import java.util.List;
import java.util.Set;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Mono;

@ExtendWith(MockitoExtension.class)
class SimilarProductsServiceTest {
    @InjectMocks
    private SimilarProductsService similarProductsService;

    @Mock
    private ServiceSimilarProductsMapper mapper;

    @Mock
    private ProductRepositoryPort port;

    @Mock
    private SimilarProductsConfigurationProperties properties;

    @Nested
    class HappyPath {
        @ParameterizedTest
        @MethodSource("dev.molaya.tests.application.service.Stubs#validInputs")
        void returnsProducts(final GetSimilarProductsInput input) {
            final var command = Stubs.validCommand(input.productId());
            when(mapper.toProductCommandSafe(Mockito.any())).thenReturn(command);
            when(port.getSimilarProductIds(Mockito.any())).thenReturn(Mono.just(productIds()));
            when(properties.limit()).thenReturn(2);
            when(properties.parallelRequests()).thenReturn(1);
            when(port.getProductDetail(Mockito.any()))
                    .thenAnswer(inv -> Mono.just(Stubs.product(
                            inv.getArgument(0, ProductCommand.class).id())));

            final var result = similarProductsService
                    .getSimilarProducts(input)
                    .collectList()
                    .block();

            assertNotNull(result);
            assertEquals(2, result.size());
        }
    }

    @Nested
    class CornerCases {
        @ParameterizedTest
        @MethodSource("dev.molaya.tests.application.service.Stubs#productIdLists")
        void handlesLimits(final Set<String> ids) {
            final var input = Stubs.validInput("1");
            final var command = Stubs.validCommand(input.productId());
            when(mapper.toProductCommandSafe(Mockito.any())).thenReturn(command);
            when(port.getSimilarProductIds(Mockito.any())).thenReturn(Mono.just(ids));
            when(properties.limit()).thenReturn(1);
            when(properties.parallelRequests()).thenReturn(1);
            lenient()
                    .when(port.getProductDetail(any()))
                    .thenAnswer(inv -> Mono.just(Stubs.product(
                            inv.getArgument(0, ProductCommand.class).id())));

            final var result = similarProductsService
                    .getSimilarProducts(input)
                    .collectList()
                    .block();

            assertNotNull(result);
            assertTrue(result.size() <= 1);
        }
    }

    @Nested
    class InvalidParameters {
        @ParameterizedTest
        @ValueSource(strings = {"", " ", "null"})
        void invalidInput_returnsEmpty(final String id) {
            final var input = validInput(id);
            final var command = new ProductCommand(id);
            when(mapper.toProductCommandSafe(any())).thenReturn(command);
            lenient().when(port.getSimilarProductIds(any())).thenReturn(Mono.just(Set.of()));
            when(properties.limit()).thenReturn(1);
            when(properties.parallelRequests()).thenReturn(1);

            final List<Product> result = similarProductsService
                    .getSimilarProducts(input)
                    .collectList()
                    .block();
            assertNotNull(result);
            assertTrue(result.isEmpty());
        }

        @Test
        void nullInput_throwsIntegrationException() {
            assertThrows(
                    IntegrationException.class,
                    () -> similarProductsService.getSimilarProducts(null).blockLast());
        }
    }

    @Nested
    class Exceptions {
        @Test
        void similarIdsThrows_throwsIntegrationException() {
            final var input = validInput("1");
            final var command = validCommand(input.productId());
            when(mapper.toProductCommandSafe(any())).thenReturn(command);
            when(port.getSimilarProductIds(any())).thenThrow(new RuntimeException("failed"));
            when(properties.limit()).thenReturn(1);
            when(properties.parallelRequests()).thenReturn(1);
            final var flux = similarProductsService.getSimilarProducts(input);
            assertThrows(IntegrationException.class, flux::blockLast);
        }

        @Test
        void productDetailThrows_throwsIntegrationException() {
            final var input = Stubs.validInput("1");
            final var command = Stubs.validCommand(input.productId());
            when(mapper.toProductCommandSafe(any())).thenReturn(command);
            when(port.getSimilarProductIds(any())).thenReturn(Mono.just(Set.of("2")));
            when(properties.limit()).thenReturn(1);
            when(properties.parallelRequests()).thenReturn(1);
            when(port.getProductDetail(any())).thenThrow(new RuntimeException("fail"));
            final var flux = similarProductsService.getSimilarProducts(input);
            assertThrows(IntegrationException.class, flux::blockLast);
        }
    }
}
