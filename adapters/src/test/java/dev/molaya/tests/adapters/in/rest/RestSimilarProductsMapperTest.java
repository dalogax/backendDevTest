package dev.molaya.tests.adapters.in.rest;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import dev.molaya.tests.adapters.Stubs;
import dev.molaya.tests.domain.Product;
import dev.molaya.tests.domain.exceptions.BadParametersException;
import java.math.BigDecimal;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mapstruct.factory.Mappers;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatusCode;
import reactor.core.publisher.Flux;

@ExtendWith(MockitoExtension.class)
class RestSimilarProductsMapperTest {
    private final RestSimilarProductsMapper mapper = Mappers.getMapper(RestSimilarProductsMapper.class);

    @Test
    void toRestProductDetail() {
        final var product = Product.builder()
                .id("1")
                .name("Product 1")
                .price(BigDecimal.valueOf(100.0))
                .available(true)
                .build();
        final var productDetail = mapper.toRestProductDetail(product);
        assertNotNull(productDetail);
        assertEquals(product.available(), productDetail.getAvailability());
        assertEquals(product.id(), productDetail.getId());
        assertEquals(product.name(), productDetail.getName());
        assertEquals(product.price(), productDetail.getPrice());
    }

    @Test
    void toRestProductDetail_NotAvailable() {
        final var product = Product.builder()
                .id("2")
                .name("Product 2")
                .price(BigDecimal.valueOf(200.0))
                .available(false)
                .build();
        final var productDetail = mapper.toRestProductDetail(product);
        assertNotNull(productDetail);
        assertEquals(product.available(), productDetail.getAvailability());
    }

    @Test
    void toRestProductDetail_NullProduct() {
        final var productDetail = mapper.toRestProductDetail(null);
        assertNull(productDetail);
    }

    @Test
    void wrapResponseEntityOk() {
        final var products = Flux.just(Stubs.productDetail());
        final var responseEntity = mapper.wrapAsOkResponse(products).block();
        assertNotNull(responseEntity);
        assertEquals(HttpStatusCode.valueOf(200), responseEntity.getStatusCode());
    }

    @Test
    void toGetSimilarProductsInput() {
        final var productId = "123";
        final var input = mapper.toGetSimilarProductsInput(productId);
        assertNotNull(input);
        assertEquals(productId, input.productId());
    }

    @Test
    void toGetSimilarProductsInput_OnNullThrowsBadParametersException() {
        final var exception = assertThrows(BadParametersException.class, () -> mapper.toGetSimilarProductsInput(null));
        assertNotNull(exception);
        assertEquals("Product id should not be null", exception.getMessage());
    }
}
