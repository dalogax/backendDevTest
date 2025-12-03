package com.zara.similarproducts.infrastructure.adapter.out.external;

import com.zara.similarproducts.domain.model.Product;
import com.zara.similarproducts.infrastructure.adapter.out.external.dto.ExternalProductResponse;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;

class ExternalProductMapperTest {

    private final ExternalProductMapper mapper = new ExternalProductMapper();

    @Test
    void shouldMapExternalProductResponseToDomain() {
        // Given
        ExternalProductResponse response = new ExternalProductResponse(
                "1", "Test Product", new BigDecimal("99.99"), true);
        
        // When
        Product product = mapper.toDomain(response);
        
        // Then
        assertThat(product.id().value()).isEqualTo("1");
        assertThat(product.name()).isEqualTo("Test Product");
        assertThat(product.price()).isEqualByComparingTo("99.99");
        assertThat(product.availability()).isTrue();
    }
}