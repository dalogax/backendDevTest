package com.napptilus.technicalTest.unit.controllers;

import com.napptilus.technicalTest.application.services.ProductService;
import com.napptilus.technicalTest.domain.models.Product;
import com.napptilus.technicalTest.infrastructure.controllers.ProductController;
import com.napptilus.technicalTest.infrastructure.data.dto.ProductDto;
import com.napptilus.technicalTest.infrastructure.data.mappers.ProductMapper;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.springframework.http.ResponseEntity;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.when;

class ProductControllerUnitTest {

    ProductController productController;
    MockedStatic<ProductMapper> productMapper;

    @BeforeEach
    void setup() {
        ProductService productService = Mockito.mock(ProductService.class);
        this.productMapper = mockStatic(ProductMapper.class);
        this.productController = new ProductController(productService);

        Product product = new Product(1, "producto", 1d, true);

        ProductDto productDto = new ProductDto(1, "producto", 1d, true);

        List<Product> products = new ArrayList<>();
        products.add(product);

        when(productService.findRelatedProducts(2)).thenReturn(products);
        productMapper.when(() -> ProductMapper.fromDomain(product)).thenReturn(productDto);

    }

    @AfterEach
    void close() {
        this.productMapper.close();
    }

    @Test
    void shouldGetAllProducts() {
        ResponseEntity<List<ProductDto>> response = productController.getRelatedProducts(2);
        ProductDto responseProductDto = response.getBody().get(0);

        assertEquals(1, responseProductDto.id(), "Product Controller - Unit Test - Incorrect id");
        assertEquals("producto", responseProductDto.name(), "Product Controller - Unit Test - Incorrect name");
        assertEquals(1d, responseProductDto.price(), "Product Controller - Unit Test - Incorrect Price");
        assertEquals(true, responseProductDto.availability(), "Product Controller - Unit Test - Incorrect Availability");
    }

}
