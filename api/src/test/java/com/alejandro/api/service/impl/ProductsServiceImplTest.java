package com.alejandro.api.service.impl;

import com.alejandro.api.dto.ProductDetailDTO;
import com.alejandro.api.mapper.ProductDetailMapper;
import com.alejandro.api.model.ProductDetailEntity;
import com.alejandro.api.repository.impl.ProductsRepositoryImpl;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class ProductsServiceImplTest {

    private String productId;

    private List<String> productIds;

    private ProductDetailEntity productDetailEntity;

    private ProductDetailDTO productDetailDto;

    @Mock
    private ProductsRepositoryImpl productsRepository;

    @Mock
    private ProductDetailMapper productDetailMapper;

    @InjectMocks
    private ProductsServiceImpl productsService;

    @BeforeEach
    public void setUp(){
        productId = "1";

        productIds = new ArrayList<>();
        productIds.add("2");
        productIds.add("3");
        productIds.add("4");

        productDetailEntity = ProductDetailEntity
                .builder()
                .id("2")
                .name("Product name")
                .price(10)
                .availability(true)
                .build();

        productDetailDto = ProductDetailDTO
                .builder()
                .id("2")
                .name("Product name")
                .price(10)
                .availability(true)
                .build();
    }

    @Test
    @DisplayName("Should return when get similar product ids.")
    @Order(1)
    public void getSimilarProductIds() {
        // Given
        List<String> actualProductIds = new ArrayList<>();
        actualProductIds.add("2");
        actualProductIds.add("3");
        actualProductIds.add("4");

        // When
        Mockito.when(productsRepository.getSimilarProductIds(productId)).thenReturn(Optional.of(productIds));
        List<String> result = productsService.getSimilarProductIds(productId).get();

        // Then
        assertNotNull(result);
        assertEquals(result.size(), actualProductIds.size());
        assertEquals(result.get(0), actualProductIds.get(0));
    }

    @Test
    @DisplayName("Should return when get product detail.")
    @Order(2)
    public void getProductDetail() {
        // Given
        String actualProductId = "2";
        String actualProductName = "Product name";

        // When
        Mockito.when(productsRepository.getProductDetail(productId)).thenReturn(Optional.of(productDetailEntity));
        Mockito.when(productDetailMapper.productEntityToDto(productDetailEntity)).thenReturn(productDetailDto);
        ProductDetailDTO result = productsService.getProductDetail(productId).get();

        // Then
        assertNotNull(result);
        assertEquals(result.getId(), actualProductId);
        assertEquals(result.getName(), actualProductName);
    }
}