package com.alejandro.api.repository.impl;

import com.alejandro.api.model.ProductDetailEntity;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class ProductsRepositoryImplTest {

    private String productId;

    private ResponseEntity<List<String>> responseProductIds;

    private ResponseEntity<ProductDetailEntity> responseProductDetail;

    @Mock
    private RestTemplate restTemplate;

    @InjectMocks
    private ProductsRepositoryImpl productsRepository;

    @BeforeEach
    public void setUp(){
        productId = "1";

        List<String> productIds = new ArrayList<>();
        productIds.add("2");
        productIds.add("3");

        responseProductIds = new ResponseEntity<>(productIds, HttpStatus.OK);

        ProductDetailEntity productDetailEntity = ProductDetailEntity
                .builder()
                .id("2")
                .name("Product name")
                .price(10)
                .availability(true)
                .build();

        responseProductDetail = new ResponseEntity<>(productDetailEntity, HttpStatus.OK);
    }

    @Test
    @DisplayName("Should return when get similar product ids.")
    @Order(1)
    public void getSimilarProductIds_OK() {
        // Given
        List<String> actualProductIds = new ArrayList<>();
        actualProductIds.add("2");
        actualProductIds.add("3");

        // When
        Mockito.when(restTemplate.exchange(
                "http://localhost:3001/product/{productId}/similarids",
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<List<String>>() { },
                productId
        )).thenReturn(responseProductIds);
        List<String> result = productsRepository.getSimilarProductIds(productId).get();

        // Then
        assertNotNull(result);
        assertEquals(result.size(), actualProductIds.size());
        assertEquals(result.get(0), actualProductIds.get(0));
    }

    @Test
    @DisplayName("Should return when get product detail.")
    @Order(3)
    public void getProductDetail() {
        // Given
        String actualProductId = "2";
        String actualProductName = "Product name";

        // When
        Mockito.when(restTemplate.exchange(
                "http://localhost:3001/product/{productId}",
                HttpMethod.GET,
                null,
                ProductDetailEntity.class,
                productId
        )).thenReturn(responseProductDetail);
        ProductDetailEntity result = productsRepository.getProductDetail(productId).get();

        // Then
        assertNotNull(result);
        assertEquals(result.getId(), actualProductId);
        assertEquals(result.getName(), actualProductName);
    }
}