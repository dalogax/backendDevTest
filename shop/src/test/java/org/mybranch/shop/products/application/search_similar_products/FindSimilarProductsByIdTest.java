package org.mybranch.shop.products.application.search_similar_products;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.mybranch.shop.products.domain.ProductDetail;
import org.mybranch.shop.products.domain.ProductNotFoundError;
import org.mybranch.shop.products.infrastructure.clients.ProductsRestClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doReturn;

@DisplayName("FindSimilarProductsById tests")
@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = {FindSimilarProductsByIdTest.ConfigTest.class})
class FindSimilarProductsByIdTest {

    @Autowired
    FindSimilarProductsById findSimilarProductsById;

    @Autowired
    ProductsRestClient productsRestClient;

    @BeforeEach
    void setup() {
    }

    @TestConfiguration
    static class ConfigTest {

        @Bean
        public ProductsRestClient productsRestClient() {
            return Mockito.mock(ProductsRestClient.class);
        }

        @Bean
        public FindSimilarProductsById findSimilarProductsById(ProductsRestClient productsRestClient) {
            return new FindSimilarProductsById(productsRestClient);
        }

    }

    @Test
    void return_not_found() {
        doReturn(Collections.emptyList())
                .when(productsRestClient)
                .similarIds(anyString());

        assertThrows(ProductNotFoundError.class, () -> {
            findSimilarProductsById.findSimilarProductsById("1");
        });
    }

    @Test
    void return_filled_products_list() {

        List<String> similarIdsMock = List.of("1");

        doReturn(similarIdsMock)
                .when(productsRestClient)
                .similarIds(anyString());

        ProductDetail productDetailMock = new ProductDetail();
        productDetailMock.setAvailability(true);
        productDetailMock.setId("1");
        productDetailMock.setName("Unreal product");
        productDetailMock.setPrice(BigDecimal.valueOf(101d));

        doReturn(productDetailMock)
                .when(productsRestClient)
                .productDetail("1");

        List<ProductDetail> similarProducts = findSimilarProductsById.findSimilarProductsById("1");

        assertNotNull(similarProducts);
        assertEquals(1, similarProducts.size(), "Wrong similar products length");
        assertEquals("1", similarProducts.get(0).getId(), "Wrong similar product id");
        assertEquals("Unreal product", similarProducts.get(0).getName(), "Wrong similar product name");
        assertTrue(similarProducts.get(0).getAvailability(), "Wrong similar product availability");

    }

}