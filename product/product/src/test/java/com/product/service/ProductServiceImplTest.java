package com.product.service;

import com.product.client.ProductClient;
import com.product.dto.ProductDetailDTO;
import com.product.dto.SimilarProductDTO;
import com.product.service.impl.ProductServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class ProductServiceImplTest {
    
    @Mock
    private ProductClient productClient;
    
    @InjectMocks
    private ProductServiceImpl productService;
    
    private static final String PRODUCT_ID= "1";
    
    private static final List<Integer> SIMILAR_IDS= List.of(1,2,3);
    
    @Test
    void when_productIdIsNull_then_returnNull(){
        SimilarProductDTO result = productService.findSimilarProducts(null);
        assertThat(result).isNull();
    }
    
    @Test
    void when_similarProductsIdsIsNull_then_returnNull(){
        when(productClient.findSimilarProductsIds(PRODUCT_ID)).thenReturn(null);
        SimilarProductDTO result = productService.findSimilarProducts(PRODUCT_ID);
        assertThat(result).isNull();
    }
    @Test
    void when_productDetailsIsNull_then_returnNull(){
        when(productClient.findSimilarProductsIds(PRODUCT_ID)).thenReturn(SIMILAR_IDS);
        when(productClient.findProductById(Integer.valueOf(PRODUCT_ID))).thenReturn(null);
        SimilarProductDTO result = productService.findSimilarProducts(PRODUCT_ID);
        assertThat(result).isNull();
    }

    @Test
    void when_productDetailsIsNotNull_then_returnSimilarProduct(){
        ProductDetailDTO productDetailDTO = ProductDetailDTO.builder().id("1").price(23).name("product").availability(true).build();
        when(productClient.findSimilarProductsIds(PRODUCT_ID)).thenReturn(SIMILAR_IDS);
        when(productClient.findProductById(Integer.valueOf(PRODUCT_ID))).thenReturn(productDetailDTO);
        SimilarProductDTO result = productService.findSimilarProducts(PRODUCT_ID);
        assertThat(result).isNotNull();
    }

    
    

   
}
