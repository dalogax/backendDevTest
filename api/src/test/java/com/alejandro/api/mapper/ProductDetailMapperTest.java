package com.alejandro.api.mapper;

import com.alejandro.api.dto.ProductDetailDTO;
import com.alejandro.api.model.ProductDetailEntity;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class ProductDetailMapperTest {

    private ProductDetailEntity productDetailEntity;

    private ProductDetailDTO productDetailDto;

    private List<ProductDetailEntity> productDetailEntityList;

    private List<ProductDetailDTO> productDetailDtoList;

    @Mock
    private ProductDetailEntity productDetailEntityMock;

    @Mock
    private ProductDetailDTO productDetailDtoMock;

    @InjectMocks
    private ProductDetailMapperImpl productDetailMapper;

    @BeforeEach
    public void setUp(){
        productDetailMapper = new ProductDetailMapperImpl();

        productDetailEntity = ProductDetailEntity
                .builder()
                .id("1")
                .name("Product name")
                .price(10)
                .availability(true)
                .build();

        productDetailDto = ProductDetailDTO
                .builder()
                .id("1")
                .name("Product name")
                .price(10)
                .availability(true)
                .build();

        productDetailEntityList = new ArrayList<>();
        productDetailEntityList.add(productDetailEntity);

        productDetailDtoList = new ArrayList<>();
        productDetailDtoList.add(productDetailDto);
    }

    @Test
    @DisplayName("Should return when convert productDTO to productEntity.")
    @Order(1)
    public void productDtoToEntity() {
        // Given
        String actualProductId = "1";
        String actualProductName = "Product name";

        // When
        ProductDetailEntity result = productDetailMapper.productDtoToEntity(productDetailDto);

        // Then
        assertNotNull(result);
        assertEquals(result.getId(), actualProductId);
        assertEquals(result.getName(), actualProductName);
    }

    @Test
    @DisplayName("Should return when convert productEntity to productDTO.")
    @Order(2)
    public void productEntityToDto() {
        // Given
        String actualProductId = "1";
        String actualProductName = "Product name";

        // When
        ProductDetailDTO result = productDetailMapper.productEntityToDto(productDetailEntity);

        // Then
        assertNotNull(result);
        assertEquals(result.getId(), actualProductId);
        assertEquals(result.getName(), actualProductName);
    }

    @Test
    @DisplayName("Should return when convert productEntityList to productDtoList.")
    @Order(3)
    public void mapProductEntityListToDtoList() {
        // Given
        List<ProductDetailDTO> actualProductDtoList = new ArrayList<>();
        actualProductDtoList.add(productDetailDto);

        // When
        List<ProductDetailDTO> result = productDetailMapper.mapProductEntityListToDtoList(productDetailEntityList);

        // Then
        assertNotNull(result);
        assertEquals(result.size(), actualProductDtoList.size());
        assertEquals(result.get(0).getId(), actualProductDtoList.get(0).getId());
        assertEquals(result.get(0).getName(), actualProductDtoList.get(0).getName());
    }

    @Test
    @DisplayName("Should return when convert productDtoList to productEntityList.")
    @Order(4)
    public void mapProductDtoListToEntityList() {
        // Given
        List<ProductDetailEntity> actualProductEntitiList = new ArrayList<>();
        actualProductEntitiList.add(productDetailEntity);

        // When
        List<ProductDetailEntity> result = productDetailMapper.mapProductDtoListToEntityList(productDetailDtoList);

        // Then
        assertNotNull(result);
        assertEquals(result.size(), actualProductEntitiList.size());
        assertEquals(result.get(0).getId(), actualProductEntitiList.get(0).getId());
        assertEquals(result.get(0).getName(), actualProductEntitiList.get(0).getName());
    }
}