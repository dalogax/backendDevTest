package com.product.controller;

import com.product.dto.SimilarProductDTO;
import com.product.service.ProductService;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class ProductControllerTest {

    @InjectMocks
    private ProductController productController;

    @Mock
    private ProductService productService;
    
    private static final String PRODUCT_ID = "1";

    @Test
     void when_callGetProductsById_then_returnOKStatus() {
        
        SimilarProductDTO similarProductDTO = new SimilarProductDTO();
        when(productService.findSimilarProducts(PRODUCT_ID)).thenReturn(similarProductDTO);

        
        ResponseEntity<SimilarProductDTO> responseEntity = productController.getProductsById(PRODUCT_ID);


        Assertions.assertThat(HttpStatus.OK).isEqualTo( responseEntity.getStatusCode());
        Assertions.assertThat(similarProductDTO).isEqualTo(responseEntity.getBody());
    }
}
