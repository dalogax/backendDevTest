package com.sngular.similarproducts.application;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.util.Set;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import com.sngular.similarproducts.domain.ProductDetail;

@ExtendWith(MockitoExtension.class)
class DetailProductsUseCaseTest {

    @InjectMocks
    private DetailProductsUseCase detailProductsUseCase;

    @Test
    void shouldReturnSimilarProducts() {
        String productId = "1";

        Set<ProductDetail> result = detailProductsUseCase.getSimilarProducts(productId);

        assertNotNull(result);
        assertEquals(2, result.size());

        var product2 = result.iterator().next();
        assertEquals("2", product2.id());
        assertEquals("Skoda Enyaq", product2.name());
        assertEquals(40000, product2.price());
        assertEquals(true, product2.availability());

        var product3 = result.iterator().next();
        assertEquals("3", product3.id());
        assertEquals("Byd Tang", product3.name());
        assertEquals(38000, product3.price());
        assertEquals(false, product3.availability());
    }

}
