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
public class SimilarProductsServiceImplTest {

    private String productId;

    private String similarProductId;

    private List<String> productIds;

    private ProductDetailDTO productDetailDto;

    @Mock
    private ProductsServiceImpl productsService;

    @InjectMocks
    private SimilarProductsServiceImpl similarProductsService;

    @BeforeEach
    public void setUp(){
        productId = "1";

        similarProductId = "2";

        productIds = new ArrayList<>();
        productIds.add("2");

        productDetailDto = ProductDetailDTO
                .builder()
                .id("2")
                .name("Product name")
                .price(10)
                .availability(true)
                .build();
    }

    @Test
    @DisplayName("Should return when get similar products.")
    @Order(1)
    public void getSimilarProducts() {
        // Given
        ProductDetailDTO productDetailDTO = ProductDetailDTO
                .builder()
                .id("2")
                .name("Product name")
                .price(10)
                .availability(true)
                .build();

        List<ProductDetailDTO> actualProductDetailDtoList = new ArrayList<>();
        actualProductDetailDtoList.add(productDetailDTO);

        // When
        Mockito.when(productsService.getSimilarProductIds(productId)).thenReturn(Optional.of(productIds));
        Mockito.when(productsService.getProductDetail(similarProductId)).thenReturn(Optional.of(productDetailDto));
        List<ProductDetailDTO> result = similarProductsService.getSimilarProducts(productId);

        // Then
        assertNotNull(result);
        assertEquals(result.size(), actualProductDetailDtoList.size());
        assertEquals(result.get(0).getId(), actualProductDetailDtoList.get(0).getId());
        assertEquals(result.get(0).getName(), actualProductDetailDtoList.get(0).getName());
    }
}