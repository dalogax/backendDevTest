package dev.molaya.tests.application.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import dev.molaya.tests.application.in.dto.GetSimilarProductsInput;
import dev.molaya.tests.domain.exceptions.BadParametersException;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

class ServiceSimilarProductsMapperTest {
    private final ServiceSimilarProductsMapper mapper = Mappers.getMapper(ServiceSimilarProductsMapper.class);

    @Test
    void toProductCommand() {
        final var input = new GetSimilarProductsInput("12345");
        final var result = mapper.toProductCommand(input);
        assertNotNull(result);
        assertEquals(input.productId(), result.id());
    }

    @Test
    void toProductCommand_NullInput() {
        final var result = mapper.toProductCommand(null);
        assertNull(result);
    }

    @Test
    void toProductCommandSafe_NullInput() {
        final var exception = assertThrows(BadParametersException.class, () -> mapper.toProductCommandSafe(null));
        assertEquals("Product id should not be null", exception.getMessage());
    }
}
