package com.example.proxy.unit.service;

import com.example.proxy.client.ProductClient;
import com.example.proxy.dto.ProductDetailDTO;
import com.example.proxy.dto.SimilarProductsDTO;
import com.example.proxy.service.ProductService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.mockito.Mockito.*;

@SpringBootTest
@AutoConfigureMockMvc
public class ProductServiceTest {
    @Autowired
    private ProductService productService;

    @MockBean
    ProductClient productClient;

    @Test
    public void getSimilarProductsOk() {
        SimilarProductsDTO similarProductsDTO = new SimilarProductsDTO();
        Set<String> ids = new HashSet<>();
        ids.add("1");
        ids.add("2");
        similarProductsDTO.setIds(ids);

        List<ProductDetailDTO> productDetailDTOList = new ArrayList<>();
        ProductDetailDTO productDetailDTO1 = new ProductDetailDTO("1", "t-shirt", 10, true);
        ProductDetailDTO productDetailDTO2 = new ProductDetailDTO("2", "jean", 30, true);
        productDetailDTOList.add(productDetailDTO1);
        productDetailDTOList.add(productDetailDTO2);

        when(productClient.getSimilarProducts(anyString())).thenReturn(similarProductsDTO);
        when(productClient.getProductDetail(anyString())).thenReturn(productDetailDTO1).thenReturn(productDetailDTO2);

        List<ProductDetailDTO> productDetailDTOS = productService.getSimilarProducts("0");

        assertEquals(productDetailDTOS.get(0), productDetailDTO1);
        assertEquals(productDetailDTOS.get(1), productDetailDTO2);
        verify(productClient, atLeast(1)).getSimilarProducts(any());
        verify(productClient, atLeast(2)).getProductDetail(any());

    }

}
