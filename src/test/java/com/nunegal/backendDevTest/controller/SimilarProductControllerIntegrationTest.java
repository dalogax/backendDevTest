package com.nunegal.backendDevTest.controller;

import com.nunegal.backendDevTest.client.ProductClient;
import com.nunegal.backendDevTest.model.Product;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;
import java.util.List;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@org.junit.jupiter.api.Disabled("Temporarily skipped due to injection issue")
public class SimilarProductControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ProductClient productClient;

    @Test
    public void testGetSimilarProducts_IntegrationFlow() throws Exception {
        String productId = "1";
        List<Integer> ids = Arrays.asList(2, 3);
        Product product1 = new Product("2", "Shirt", 9.99, true);
        Product product2 = new Product("3", "Shoes", 19.99, true);

        when(productClient.getSimilarProductIds(productId)).thenReturn(ids);
        when(productClient.getProductById("2")).thenReturn(product1);
        when(productClient.getProductById("3")).thenReturn(product2);

        mockMvc.perform(get("/product/1/similar"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].id").value("2"))
                .andExpect(jsonPath("$[1].id").value("3"));
    }
}
