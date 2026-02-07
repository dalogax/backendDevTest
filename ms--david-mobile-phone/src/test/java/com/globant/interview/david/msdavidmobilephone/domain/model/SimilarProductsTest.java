package com.globant.interview.david.msdavidmobilephone.domain.model;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class SimilarProductsTest {

    @Test
    void shouldCreateSimilarProducts() {
        Product p1 = new Product("1", "iPhone", new BigDecimal("999"), true);
        Product p2 = new Product("2", "Samsung", new BigDecimal("899"), true);

        SimilarProducts similarProducts = new SimilarProducts(List.of(p1, p2));

        assertEquals(2, similarProducts.products().size());
        assertTrue(similarProducts.products().contains(p1));
        assertTrue(similarProducts.products().contains(p2));
    }

    @Test
    void shouldReturnEmptyListWhenNoProducts() {
        SimilarProducts similarProducts = SimilarProducts.empty();

        assertTrue(similarProducts.products().isEmpty());
    }

    @Test
    void shouldReturnImmutableList() {
        Product p1 = new Product("1", "iPhone", new BigDecimal("999"), true);
        List<Product> originalList = List.of(p1);

        SimilarProducts similarProducts = new SimilarProducts(originalList);

        assertThrows(UnsupportedOperationException.class,
                () -> similarProducts.products().add(p1));
    }

    @Test
    void shouldDefensiveCopyList() {
        Product p1 = new Product("1", "iPhone", new BigDecimal("999"), true);
        List<Product> mutableList = new java.util.ArrayList<>(List.of(p1));

        SimilarProducts similarProducts = new SimilarProducts(mutableList);
        mutableList.clear();

        assertEquals(1, similarProducts.products().size());
    }
}
