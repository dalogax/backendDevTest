package com.zara.similarproducts.infrastructure.adapter.out.external.dto;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;

class ExternalProductResponseTest {

    @Test
    void shouldCreateExternalProductResponse() {
        // Given
        String id = "1";
        String name = "Test Product";
        BigDecimal price =new BigDecimal( 99.99);
        Boolean availability = true;
        
        // When
        ExternalProductResponse response = new ExternalProductResponse(id, name, price, availability);
        
        // Then
        assertThat(response.id()).isEqualTo(id);
        assertThat(response.name()).isEqualTo(name);
        assertThat(response.price()).isEqualTo(price);
        assertThat(response.availability()).isEqualTo(availability);
    }
}