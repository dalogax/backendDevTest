package com.zara.similarproducts.domain.model;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.*;

class ProductIdTest {

    @Test
    void shouldCreateValidProductId() {
        ProductId productId = ProductId.of("123");
        
        assertThat(productId.value()).isEqualTo("123");
    }

    @Test
    void shouldThrowExceptionForNullValue() {
        assertThatThrownBy(() -> ProductId.of(null))
                .isInstanceOf(NullPointerException.class)
                .hasMessage("Product ID cannot be null");
    }

    @Test
    void shouldThrowExceptionForBlankValue() {
        assertThatThrownBy(() -> ProductId.of(""))
                .isInstanceOf(InvalidProductIdException.class);
        
        assertThatThrownBy(() -> ProductId.of("   "))
                .isInstanceOf(InvalidProductIdException.class);
    }

    @Test
    void shouldThrowExceptionForNonNumericValue() {
        assertThatThrownBy(() -> ProductId.of("abc"))
                .isInstanceOf(InvalidProductIdException.class);
        
        assertThatThrownBy(() -> ProductId.of("12a"))
                .isInstanceOf(InvalidProductIdException.class);
    }

    @Test
    void shouldAcceptNumericValues() {
        assertThatCode(() -> ProductId.of("123")).doesNotThrowAnyException();
        assertThatCode(() -> ProductId.of("0")).doesNotThrowAnyException();
    }
}