package dev.molaya.tests.adapters.out.client;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

import dev.molaya.tests.adapters.Stubs;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

class ClientSimilarProductsMapperTest {
    private final ClientSimilarProductsMapper mapper = Mappers.getMapper(ClientSimilarProductsMapper.class);

    @Test
    void toDomainProduct() {
        final var source = Stubs.productDetailOUT();
        final var result = mapper.toDomainProduct(source);
        assertNotNull(result);
        assertEquals(source.getAvailability(), result.available());
        assertEquals(source.getId(), result.id());
        assertEquals(source.getName(), result.name());
        assertEquals(source.getPrice(), result.price());
    }

    @Test
    void toDomainProduct_NullSource() {
        final var result = mapper.toDomainProduct(null);
        assertNull(result);
    }
}
