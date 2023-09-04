package com.napptilus.technicalTest.unit.services;

import com.napptilus.technicalTest.application.services.ProductService;
import com.napptilus.technicalTest.domain.models.Product;
import com.napptilus.technicalTest.domain.repositories.ProductRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.description;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class ProductServiceUnitTest {

    @InjectMocks
    ProductService productService;

    @Mock
    ProductRepository productRepository;

    @BeforeEach
    void setup() {
        Product product1 = new Product(1, "producto", 1d, true);
        Product product2 = new Product(3, "producto", 1d, true);

        List<Product> products = new ArrayList<>();
        products.add(product1);
        products.add(product2);

        List<Integer> relatedProducts = new ArrayList<>();
        relatedProducts.add(1);
        relatedProducts.add(3);

        Mockito.when(productRepository.findRelatedIds(2)).thenReturn(relatedProducts);
        Mockito.when(productRepository.findProductsById(relatedProducts)).thenReturn(products);
    }

    @Test
    void shouldReturnRelatedProducts() {
        List<Product> result = this.productService.findRelatedProducts(2);

        List<Integer> relatedProducts = new ArrayList<>();
        relatedProducts.add(1);
        relatedProducts.add(3);

        verify(productRepository, description("Product Service - Unit test - Repository mock findRelatedIds was not called"))
                .findRelatedIds(2);

        verify(productRepository, description("Product Service - Unit test - Repository mock findProductsById was not called"))
                .findProductsById(relatedProducts);

        assertEquals(2, result.size(), "Product Service - Unit Test - Incorrect size of response");
        assertEquals(1, result.get(0).getId(), "Product Service - Unit Test - Incorrect id 1");
        assertEquals(3, result.get(1).getId(), "Product Service - Unit Test - Incorrect id 2");
    }

}
