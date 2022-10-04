package com.dtsanchezz.similarProducts.service;

import com.dtsanchezz.similarProducts.consumer.api.ProductConsumer;
import com.dtsanchezz.similarProducts.model.ProductDetail;
import com.dtsanchezz.similarProducts.service.impl.ProductServiceImpl;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class ProductServiceTest {

    @Mock
    private ProductConsumer productConsumer;

    @InjectMocks
    private ProductServiceImpl underTest;

    public static String PRODUCT_ID_TO_TEST = "1";

    final ProductDetail productTwo = new ProductDetail("2", "Test TWO", 2.99, true);
    final ProductDetail productThree = new ProductDetail("3", "Test THREE", 3.99, true);
    final ProductDetail productFour = new ProductDetail("4", "Test FOUR", 4.99, true);

    final List<ProductDetail> productDetailList = List.of(this.productTwo, this.productThree, this.productFour);


    @Test
    public void getSimilarProductsSuccess() {
        when(this.productConsumer.getSimilarIds(any()))
                .thenReturn(List.of("2", "3", "4"));

        when(this.productConsumer.getProductDetail(eq("2")))
                .thenReturn(this.productTwo);

        when(this.productConsumer.getProductDetail(eq("3")))
                .thenReturn(this.productThree);

        when(this.productConsumer.getProductDetail(eq("4")))
                .thenReturn(this.productFour);

        final List<ProductDetail> response = this.underTest.getSimilarProducts(PRODUCT_ID_TO_TEST);

        Assertions.assertNotNull(response);
        Assertions.assertFalse(response.isEmpty());
        Assertions.assertEquals(response, this.productDetailList);
    }

    @Test
    public void getSimilarIdIsEmpty() {
        when(this.productConsumer.getSimilarIds(any()))
                .thenReturn(Collections.emptyList());

        final List<ProductDetail> response = this.underTest.getSimilarProducts(PRODUCT_ID_TO_TEST);
        Assertions.assertNotNull(response);
        Assertions.assertTrue(response.isEmpty());
    }
}
