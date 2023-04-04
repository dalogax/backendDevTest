package com.example.proxy.unit.controller;

import com.example.proxy.controller.ProductController;
import com.example.proxy.dto.ProductDetailDTO;
import com.example.proxy.exceptions.APIException;
import com.example.proxy.exceptions.NotFoundException;
import com.example.proxy.service.ProductService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@SpringBootTest
@AutoConfigureMockMvc
public class ProductControllerTest {
    @Autowired
    ProductController productController;
    @MockBean
    ProductService productService;
    @Test
    public void testGetSimilarProductOk() {
        //given
        List<ProductDetailDTO> productDetailDTOList = new ArrayList<>();
        ProductDetailDTO productDetailDTO1 = new ProductDetailDTO("1", "t-shirt", 10, true);
        ProductDetailDTO productDetailDTO2 = new ProductDetailDTO("2", "jean", 30, true);
        productDetailDTOList.add(productDetailDTO1);
        productDetailDTOList.add(productDetailDTO2);
        when(productService.getSimilarProducts(anyString())).thenReturn(productDetailDTOList);

        //when
        ResponseEntity<List<ProductDetailDTO>> response = productController.getSimilarProducts("1");

        //then
        verify(productService, atLeast(1)).getSimilarProducts(anyString());
        assertEquals(response.getStatusCode(), HttpStatus.OK);
        assertEquals(response.getBody(), productDetailDTOList);
    }

    @Test
    public void testGetSimilarProductThrownException() {
        //given
        when(productService.getSimilarProducts(anyString())).thenThrow(new NotFoundException("Product Not Found"));

        //then
        APIException exception = assertThrows(NotFoundException.class, () -> {
           productController.getSimilarProducts("1");
        });
        assertEquals(HttpStatus.NOT_FOUND.value(), exception.getStatus());
        assertEquals("Product Not Found", exception.getMessage());
    }
}
