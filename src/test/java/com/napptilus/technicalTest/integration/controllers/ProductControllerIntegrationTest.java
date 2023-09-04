package com.napptilus.technicalTest.integration.controllers;

import com.napptilus.technicalTest.application.services.ProductService;
import com.napptilus.technicalTest.domain.models.Product;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.ArrayList;
import java.util.List;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest
class ProductControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ProductService productService;

    @Test
    void shouldUseDefaultParams() throws Exception {

        Product product = new Product(1, "producto", 1d, true);

        List<Product> products = new ArrayList<>();
        products.add(product);

        when(productService.findRelatedProducts(2)).thenReturn(products);

        mockMvc.perform(get("/product/2/similar"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].name").value("producto"))
                .andExpect(jsonPath("$[0].price").value(1d))
                .andExpect(jsonPath("$[0].availability").value(true));

        verify(productService, description("Product Controller - Integration test - Wrong Params")).findRelatedProducts(2);

    }
}
